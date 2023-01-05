package immersive_particles.forge;

import immersive_particles.Main;
import immersive_particles.Particles;
import immersive_particles.forge.cobalt.registration.RegistrationImpl;
import immersive_particles.particles.FlyParticle;
import immersive_particles.resources.ObjectLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(Main.MOD_ID)
@Mod.EventBusSubscriber(modid = Main.MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public final class Forge {
    @SubscribeEvent
    public static void data(FMLConstructModEvent event) {
        ((ReloadableResourceManagerImpl)MinecraftClient.getInstance().getResourceManager()).registerReloader(new ObjectLoader());
    }

    public Forge() {
        RegistrationImpl.bootstrap();

        Particles.bootstrap();
    }

    @SubscribeEvent
    public static void onRegistryEvent(ParticleFactoryRegisterEvent event) {
        MinecraftClient.getInstance().particleManager.registerFactory(Particles.FLY.get(), FlyParticle.Factory::new);
    }
}
