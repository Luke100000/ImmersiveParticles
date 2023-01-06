package immersive_particles.fabric;

import immersive_particles.Main;
import immersive_particles.Particles;
import immersive_particles.fabric.cobalt.registration.RegistrationImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public final class Fabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new RegistrationImpl();

        Particles.bootstrap();

        ClientTickEvents.START_CLIENT_TICK.register(Main::tick);

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new FabricObjectLoader());

        Particles.init();
    }
}

