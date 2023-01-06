package immersive_particles.fabric.cobalt.registration;

import immersive_particles.cobalt.registration.Registration;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;
import java.util.function.Supplier;

public class RegistrationImpl extends Registration.Impl {
    @Override
    public <T> Supplier<T> register(Registry<? super T> registry, Identifier id, Supplier<T> obj) {
        T register = Registry.register(registry, id, obj.get());
        return () -> register;
    }

    @Override
    public Supplier<ParticleType<?>> simpleParticle() {
        return FabricParticleTypes::simple;
    }

    @Override
    public <T extends ParticleEffect> void registerParticleFactory(ParticleType<T> type, Function<SpriteProvider, ParticleFactory<T>> factory) {
        ParticleFactoryRegistry.getInstance().register(type, factory::apply);
    }
}
