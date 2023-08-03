package immersive_particles.forge;

import immersive_particles.Main;
import immersive_particles.resources.ObjectLoader;
import immersive_particles.resources.ParticleManagerLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraftforge.api.distmarker.Dist;
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
        ((ReloadableResourceManagerImpl)MinecraftClient.getInstance().getResourceManager()).registerReloader(new ParticleManagerLoader());
    }
}
