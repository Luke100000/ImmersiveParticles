package immersive_particles.core;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import immersive_particles.particles.ImmersiveParticle;
import immersive_particles.resources.ParticleManagerLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ImmersiveParticleManager {
    public static final AtomicInteger particleCount = new AtomicInteger();
    private static final ConcurrentLinkedQueue<ImmersiveParticle> particles = new ConcurrentLinkedQueue<>();
    private static final int MAX_PARTICLES = 1024 * 16;

    public static Frustum frustum;
    private static ClientWorld world;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    static State current = new State();
    static State last = new State();

    static private volatile boolean rendering;
    static private volatile boolean updating;

    public static void render(MatrixStack matrices, LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta) {
        lightmapTextureManager.enable();

        if (!rendering) {
            rendering = true;

            // Swap states
            State old = current;
            current = last;
            last = old;

            // Remember the camera position it has been build with
            current.camera = camera.getPos();

            executor.execute(() -> {
                // Start new tesselation
                BufferBuilder builder = current.tessellator.getBuffer();
                builder.reset();

                builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);

                // Render all particles
                for (ImmersiveParticle particle : particles) {
                    if (frustum == null || frustum.isVisible(particle.getBoundingBox())) {
                        particle.render(builder, current.camera, tickDelta);
                    }
                }

                // Finish
                builder.end();
                current.pair = builder.popData();
                rendering = false;
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
        RenderSystem.setShader(GameRenderer::getParticleShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.setShader(GameRenderer::getParticleShader);
        RenderSystem.setShaderTexture(0, ParticleManagerLoader.ATLAS_TEXTURE);

        // Yeet
        BufferBuilder.DrawArrayParameters drawArrayParameters = last.pair.getFirst();
        BufferRenderer.draw(last.pair.getSecond(), drawArrayParameters.getMode(), drawArrayParameters.getVertexFormat(), drawArrayParameters.getCount(), drawArrayParameters.getElementFormat(), drawArrayParameters.getVertexCount(), drawArrayParameters.hasNoIndexBuffer());

        // Restore
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        lightmapTextureManager.disable();
    }

    public static void addParticle(ImmersiveParticleType type, SpawnLocation location) {
        if (particleCount.get() < MAX_PARTICLES) { //todo constant
            ImmersiveParticle particle = new ImmersiveParticle(type, location);
            particles.add(particle);
            particleCount.incrementAndGet();
        }
    }

    public static ClientWorld getWorld() {
        return world;
    }

    public void tick() {
        if (!MinecraftClient.getInstance().isPaused() && !updating) {
            updating = true;
            executor.execute(() -> {
                for (Iterator<ImmersiveParticle> it = particles.iterator(); it.hasNext(); ) {
                    ImmersiveParticle particle = it.next();
                    if (particle.tick()) {
                        it.remove();
                        particleCount.decrementAndGet();
                    }
                }
                updating = false;
            });
        }
    }

    public static void setWorld(@Nullable ClientWorld world) {
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
