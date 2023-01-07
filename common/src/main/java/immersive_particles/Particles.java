package immersive_particles;

import immersive_particles.cobalt.registration.Registration;
import immersive_particles.particles.HoveringParticle;
import immersive_particles.resources.Resources;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.function.Supplier;

public class Particles {
    public static Supplier<DefaultParticleType> BUMBLEBEE = register("bumblebee", Registration.INSTANCE.simpleParticle());

    public static void bootstrap() {
        //todo here create the particles dynamically based on config

        Map<String, String> json = Resources.getResourceFiles("immersive_particles/", "json");
    }

    static Supplier<DefaultParticleType> register(String name, Supplier<ParticleType<?>> particle) {
        //noinspection unchecked
        return (Supplier<DefaultParticleType>)(Object)Registration.register(Registry.PARTICLE_TYPE, Main.locate(name), particle);
    }

    public static  <T extends ParticleEffect> void init() {
        //todo here register them to the respective factory
        Registration.registerParticleFactory(Particles.BUMBLEBEE.get(), HoveringParticle.Factory::new);
    }
}
