package immersive_particles.forge.cobalt.registration;

import immersive_particles.Particles;
import immersive_particles.cobalt.registration.Registration;
import immersive_particles.particles.HoveringParticle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistrationImpl extends Registration.Impl {
    @SuppressWarnings("unused")
    public static final RegistrationImpl IMPL = new RegistrationImpl();

    private final Map<String, RegistryRepo> repos = new HashMap<>();

    public static void bootstrap() {
    }

    private RegistryRepo getRepo(String namespace) {
        return repos.computeIfAbsent(namespace, RegistryRepo::new);
    }

    @Override
    public <T> Supplier<T> register(Registry<? super T> registry, Identifier id, Supplier<T> obj) {
        @SuppressWarnings("unchecked")
        DeferredRegister<T> reg = getRepo(id.getNamespace()).get(registry);
        return reg.register(id.getPath(), obj);
    }

    @Override
    public Supplier<ParticleType<?>> simpleParticle() {
        return () -> new DefaultParticleType(false);
    }

    @Override
    public <T extends ParticleEffect> void registerParticleFactory(ParticleType<T> type, Function<SpriteProvider, ParticleFactory<T>> factory) {
        MinecraftClient.getInstance().particleManager.registerFactory(type, factory::apply);
    }

    static class RegistryRepo {
        private final Set<Identifier> skipped = new HashSet<>();
        private final Map<Identifier, DeferredRegister<?>> registries = new HashMap<>();

        private final String namespace;

        public RegistryRepo(String namespace) {
            this.namespace = namespace;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public <T> DeferredRegister get(Registry<? super T> registry) {
            Identifier id = registry.getKey().getValue();
            if (!registries.containsKey(id) && !skipped.contains(id)) {
                ForgeRegistry reg = RegistryManager.ACTIVE.getRegistry(id);
                if (reg == null) {
                    skipped.add(id);
                    return null;
                }

                DeferredRegister def = DeferredRegister.create(Objects.requireNonNull(reg, "Registry=" + id), namespace);

                def.register(FMLJavaModLoadingContext.get().getModEventBus());

                registries.put(id, def);
            }

            return registries.get(id);
        }
    }
}
