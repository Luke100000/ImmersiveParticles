package immersive_particles.forge;

import immersive_particles.core.ImmersiveParticlesChunkManager;
import immersive_particles.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventBus {
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ImmersiveParticlesChunkManager.tick(MinecraftClient.getInstance());
        }
    }
}
