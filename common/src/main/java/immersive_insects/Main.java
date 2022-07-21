package immersive_insects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Main {
    public static final String MOD_ID = "immersive_insects";
    public static final Logger LOGGER = LogManager.getLogger();

    public static Identifier locate(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static void tick(MinecraftClient client) {
        if (client.player != null && client.world != null) {
            Vec3d pos = client.player.getPos();
            for (int i = 0; i < 128; i++) {
                client.world.addParticle(Particles.FLY, pos.x + (client.player.getRandom().nextFloat() - 0.5) * 10.0f, pos.y + (client.player.getRandom().nextFloat() - 0.5) * 10.0f, pos.z + (client.player.getRandom().nextFloat() - 0.5) * 10.0f, 0, 0, 0);
            }
        }
    }
}
