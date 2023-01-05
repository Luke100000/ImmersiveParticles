package immersive_particles.fabric;

import immersive_particles.Main;
import immersive_particles.Particles;
import immersive_particles.fabric.cobalt.registration.RegistrationImpl;
import immersive_particles.particles.FlyParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public final class Fabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new RegistrationImpl();

        Particles.bootstrap();

        ClientTickEvents.START_CLIENT_TICK.register(Main::tick);

        ParticleFactoryRegistry.getInstance().register(Particles.FLY.get(), FlyParticle.Factory::new);
    }
}

