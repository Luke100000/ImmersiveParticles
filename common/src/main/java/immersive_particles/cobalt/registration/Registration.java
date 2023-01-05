package immersive_particles.cobalt.registration;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class Registration {
    public static Impl INSTANCE;

    public static <T> Supplier<T> register(Registry<? super T> registry, Identifier id, Supplier<T> obj) {
        return INSTANCE.register(registry, id, obj);
    }

    public static abstract class Impl {
        protected Impl() {
            INSTANCE = this;
        }

        public abstract <T> Supplier<T> register(Registry<? super T> registry, Identifier id, Supplier<T> obj);

        public abstract Supplier<ParticleType<?>> simpleParticle();
    }
}
