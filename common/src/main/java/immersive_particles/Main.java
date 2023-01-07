package immersive_particles;

import immersive_particles.core.ImmersiveParticleManager;
import immersive_particles.core.ParticleChunkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Main {
    public static final String MOD_ID = "immersive_particles";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final ImmersiveParticleManager particleManager = new ImmersiveParticleManager();

    public static Identifier locate(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static void tick(MinecraftClient client) {
        ParticleChunkManager.tick(client);

        // Update the particles
        particleManager.tick();
    }
}
