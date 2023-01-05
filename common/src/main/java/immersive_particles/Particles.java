package immersive_particles;

import immersive_particles.cobalt.registration.Registration;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class Particles {
    public static Supplier<DefaultParticleType> FLY = register("fly", Registration.INSTANCE.simpleParticle());

    public static void bootstrap() {

    }

    static Supplier<DefaultParticleType> register(String name, Supplier<ParticleType<?>> particle) {
        return (Supplier<DefaultParticleType>)(Object)Registration.register(Registry.PARTICLE_TYPE, Main.locate(name), particle);
    }
}
