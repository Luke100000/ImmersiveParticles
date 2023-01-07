package immersive_particles.core;

import com.mojang.blaze3d.systems.RenderSystem;
import immersive_particles.particles.ImmersiveParticle;
import immersive_particles.resources.ParticleManagerLoader;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public class ImmersiveParticleManager {
    private static final LinkedList<ImmersiveParticle> particles = new LinkedList<>();
    private static ClientWorld world;

    public static void render(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta) {
        lightmapTextureManager.enable();

        // Prepare transform
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
        RenderSystem.applyModelViewMatrix();

        // Prepare shader
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getParticleShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.setShader(GameRenderer::getParticleShader);
        RenderSystem.setShaderTexture(0, ParticleManagerLoader.ATLAS_TEXTURE);

        // Start new tesselation
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);

        // Render all particles
        for (ImmersiveParticle particle : particles) {
            particle.render(builder, camera, tickDelta);
        }

        // Yeet
        tessellator.draw();

        // Restore
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        lightmapTextureManager.disable();
    }

    public static void addParticle(ImmersiveParticleType type, SpawnLocation location) {
        ImmersiveParticle particle = new ImmersiveParticle(type, location);
        if (particles.size() < 256 * 256) { //todo constant
            particles.add(particle);
        }
    }

    public static ClientWorld getWorld() {
        return world;
    }

    public void tick() {
        particles.removeIf(ImmersiveParticle::tick);

        //todo evaluate
        //List<ImmersiveParticle> toRemove = particles.stream().parallel().filter(ImmersiveParticle::tick).toList();
    }

    public static void setWorld(@Nullable ClientWorld world) {
        ImmersiveParticleManager.world = world;
        particles.clear();
    }
}
