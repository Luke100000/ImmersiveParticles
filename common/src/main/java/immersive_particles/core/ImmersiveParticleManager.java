package immersive_particles.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import immersive_particles.Shaders;
import immersive_particles.core.particles.ImmersiveParticle;
import immersive_particles.core.registries.ImmersiveParticles;
import immersive_particles.core.searcher.SpawnLocation;
import immersive_particles.resources.ParticleManagerLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImmersiveParticleManager {
    private static final int MAX_PARTICLES = 1024 * 16;
    private static final int PARALLELIZATION_THRESHOLD = 128;

    public static final AtomicInteger particleCount = new AtomicInteger();
    public static int updateDrops, renderDrops = 0;
    private static final ConcurrentLinkedQueue<ImmersiveParticle> particles = new ConcurrentLinkedQueue<>();

    public static Frustum frustum;
    private static ClientWorld world;

    static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("immersive-particles-worker-%d").build();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor(namedThreadFactory);

    static State current = new State();
    static State last = new State();

    static private volatile boolean rendering;
    static private volatile boolean updating;

    private static Exception updateException;
    private static Exception renderException;

    private static final Random random = new Random();

    public static void render(MatrixStack matrices, LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta) {
        lightmapTextureManager.enable();

        if (updateException != null) {
            throw new RuntimeException(updateException);
        }
        if (renderException != null) {
            throw new RuntimeException(renderException);
        }

        if (rendering) {
            renderDrops++;
        } else {
            rendering = true;

            // Swap states
            State old = current;
            current = last;
            last = old;

            // Remember the camera position it has been build with
            current.camera = camera.getPos();

            //todo prepare for multi format and blend mode
            executor.execute(() -> {
                try {
                    // Start new tesselation
                    BufferBuilder builder = current.tessellator.getBuffer();
                    builder.reset();

                    builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);

                    // Render all particles
                    for (ImmersiveParticle particle : particles) {
                        particle.visible = frustum == null || frustum.isVisible(particle.getBoundingBox());
                        if (particle.visible) {
                            particle.render(builder, current.camera, tickDelta);
                        }
                    }

                    // Finish
                    builder.end();
                    current.pair = builder.popData();
                    rendering = false;
                } catch (Exception e) {
                    renderException = e;
                }
            });
        }

        // Still rendering
        if (last.camera == null) {
            return;
        }

        // Prepare transform
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
        matrixStack.translate(last.camera.x - camera.getPos().x, last.camera.y - camera.getPos().y, last.camera.z - camera.getPos().z);
        RenderSystem.applyModelViewMatrix();

        // Prepare shader
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(() -> Shaders.IMMERSIVE_PARTICLE_CUTOUT);
        RenderSystem.enableCull();
        RenderSystem.enableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.setShaderTexture(0, ParticleManagerLoader.ATLAS_TEXTURE);
        MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().enable();

        // Yeet
        BufferBuilder.DrawArrayParameters drawArrayParameters = last.pair.getFirst();
        BufferRenderer.draw(last.pair.getSecond(), drawArrayParameters.getMode(), drawArrayParameters.getVertexFormat(), drawArrayParameters.getCount(), drawArrayParameters.getElementFormat(), drawArrayParameters.getVertexCount(), drawArrayParameters.hasNoIndexBuffer());

        // Restore
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().disable();
        lightmapTextureManager.disable();
    }

    public static void addParticle(ImmersiveParticleType type, SpawnLocation location) {
        if (particleCount.get() < MAX_PARTICLES) {
            int count = type.getMinCount() + random.nextInt(type.getMaxCount() - type.getMinCount() + 1);
            ImmersiveParticle leader = null;
            for (int i = 0; i < count; i++) {
                ImmersiveParticle particle = ImmersiveParticles.PARTICLES.get(type.behaviorIdentifier).apply(type, location, leader);
                particles.add(particle);
                particleCount.incrementAndGet();

                if (i == 0) {
                    leader = particle;
                }
            }
        }
    }

    public static ClientWorld getWorld() {
        return world;
    }

    public static void tick(MinecraftClient client) {
        if (!client.isPaused()) {
            if (updating) {
                updateDrops++;
            } else if (particleCount.get() > 0) {
                updating = true;
                executor.execute(() -> {
                    try {
                        Stream<ImmersiveParticle> stream = particleCount.get() > PARALLELIZATION_THRESHOLD ? particles.stream().parallel() : particles.stream().sequential();
                        Set<ImmersiveParticle> particles2 = stream.filter(ImmersiveParticle::tick).collect(Collectors.toSet());
                        particleCount.addAndGet(-particles2.size());
                        particles.removeAll(particles2);
                        updating = false;
                    } catch (Exception e) {
                        updateException = e;
                    }
                });
            }
        }
    }

    public static void setWorld(ClientWorld world) {
        ImmersiveParticleManager.world = world;
        particles.clear();
        particleCount.set(0);
    }

    private static final class State {
        public Tessellator tessellator;
        public Vec3d camera;
        public Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair;

        private State() {
            this.tessellator = new Tessellator();
            this.camera = null;
            this.pair = null;
        }
    }
}
