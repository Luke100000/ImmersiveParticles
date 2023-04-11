package immersive_particles.core.registries;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.spawnTypes.InBlockSpawnType;
import immersive_particles.core.spawnTypes.OnBlockSpawnType;
import immersive_particles.core.spawnTypes.SpawnType;

import java.util.HashMap;
import java.util.Map;

public class SpawnTypes {
    static public final Map<String, SpawnType> TYPES = new HashMap<>();

    static {
        register("onBlock", new OnBlockSpawnType());
        register("inBlock", new InBlockSpawnType());
    }

    public static void registerParticleType(JsonObject object, ImmersiveParticleType type) {
        TYPES.get(object.get("type").getAsString()).register(object, type);
    }

    public static void clear() {
        TYPES.values().forEach(SpawnType::clear);
    }

    private static void register(String identifier, SpawnType type) {
        TYPES.put(identifier, type);
    }
}
