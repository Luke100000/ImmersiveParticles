package immersive_particles.fabric;

import dev.architectury.event.events.client.ClientTickEvent;
import immersive_particles.Main;
import immersive_particles.Particles;
import immersive_particles.fabric.cobalt.registration.RegistrationImpl;
import immersive_particles.particles.FlyParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class Fabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new RegistrationImpl();

        Particles.bootstrap();

        ClientTickEvent.CLIENT_PRE.register(Main::tick);



        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(Main.locate("particle/fly"));
        }));

        Particles.FLY = FabricParticleTypes.simple();

        Registry.register(Registry.PARTICLE_TYPE, new Identifier(Main.MOD_ID, "fly"), Particles.FLY);

        ParticleFactoryRegistry.getInstance().register(Particles.FLY, FlyParticle.Factory::new);
    }
}

