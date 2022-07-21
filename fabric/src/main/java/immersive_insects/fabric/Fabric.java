package immersive_insects.fabric;

import immersive_insects.Particles;
import immersive_insects.fabric.cobalt.registration.RegistrationImpl;
import net.fabricmc.api.ClientModInitializer;

public final class Fabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new RegistrationImpl();

        Particles.bootstrap();
    }
}

