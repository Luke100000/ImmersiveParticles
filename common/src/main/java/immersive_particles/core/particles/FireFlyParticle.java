package immersive_particles.core.particles;

import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.searcher.SpawnLocation;
import net.minecraft.client.render.LightmapTextureManager;

public class FireFlyParticle extends RandomFly {
    public FireFlyParticle(ImmersiveParticleType type, SpawnLocation location, ImmersiveParticle leader) {
        super(type, location, leader);
    }

    private double getGlow() {
        return Math.max(0, Math.cos(age * 0.33) + Math.cos(age * 0.17) + 0.5);
    }

    @Override
    protected int getBrightness() {
        int light = super.getBrightness();
        int blockLight = Math.min(15, LightmapTextureManager.getBlockLightCoordinates(light) + (int)(getGlow() * 16));
        return LightmapTextureManager.pack(
                blockLight,
                LightmapTextureManager.getSkyLightCoordinates(light)
        );
    }
}
