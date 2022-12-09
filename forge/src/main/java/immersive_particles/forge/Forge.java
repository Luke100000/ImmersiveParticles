package immersive_particles.forge;

import immersive_particles.Main;
import immersive_particles.Particles;
import immersive_particles.forge.cobalt.registration.RegistrationImpl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod(Main.MOD_ID)
@Mod.EventBusSubscriber(modid = Main.MOD_ID, value = Dist.CLIENT, bus=Bus.MOD)
public final class Forge {
    public Forge() {
        RegistrationImpl.bootstrap();
    }

    @SubscribeEvent
    public static void onRegistryEvent(RegistryEvent<?> event) {
        Particles.bootstrap();
    }
}
