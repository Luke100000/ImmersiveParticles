package immersive_particles.cobalt.registration;

import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;
import java.util.function.Supplier;

public class Registration {
    public static Impl INSTANCE;

    public static <T> Supplier<T> register(Registry<? super T> registry, Identifier id, Supplier<T> obj) {
        return INSTANCE.register(registry, id, obj);
    }

    public static <T extends ParticleEffect> void registerParticleFactory(ParticleType<T> type, Function<SpriteProvider, ParticleFactory<T>> factory) {
        INSTANCE.registerParticleFactory(type, factory);
    }

    public static abstract class Impl {
        protected Impl() {
            INSTANCE = this;
        }

        public abstract <T> Supplier<T> register(Registry<? super T> registry, Identifier id, Supplier<T> obj);

        public abstract Supplier<ParticleType<?>> simpleParticle();

        public abstract <T extends ParticleEffect> void registerParticleFactory(ParticleType<T> type, Function<SpriteProvider, ParticleFactory<T>> factory);
    }
}
