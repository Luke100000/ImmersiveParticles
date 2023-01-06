package immersive_particles.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;

public class HoveringParticle extends ImmersiveParticle {
    public HoveringParticle(ClientWorld world, SpriteProvider spriteProvider, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, spriteProvider, x, y, z, velocityX, velocityY, velocityZ);
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;
        public static HoveringParticle.Factory INSTANCE;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
            INSTANCE = this;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz) {
            return new HoveringParticle(clientWorld, spriteProvider, x, y, z, vx, vy, vz);
        }
    }
}
