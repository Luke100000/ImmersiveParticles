package immersive_insects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Main {
    public static final String MOD_ID = "immersive_insects";
    public static final Logger LOGGER = LogManager.getLogger();

    public static Identifier locate(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static void tick(MinecraftClient client) {
        InsectChunkManager.tick(client);
    }
}
