package immersive_insects.particles;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

public class FlyParticle extends SpriteBillboardParticle {
    public FlyParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.velocityMultiplier = 0.96f;
        this.field_28787 = true;
        this.velocityX *= 0.1f;
        this.velocityY = this.velocityY * (double)0.1f;
        this.velocityZ *= 0.1f;
        float f = this.random.nextFloat() * 0.4f + 0.6f;
        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;
        this.scale *= 0.75f;
        this.maxAge = 20 * 60;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getSize(float tickDelta) {
        return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public static class Factory
            implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double x, double y, double z, double vx, double vy, double vz) {
            FlyParticle p = new FlyParticle(clientWorld, x, y, z, vx, vy, vz);
            p.setSprite(this.spriteProvider);
            return p;
        }
    }
}

