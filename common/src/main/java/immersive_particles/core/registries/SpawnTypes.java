package immersive_particles.core.registries;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.spawn_types.InBlockSpawnType;
import immersive_particles.core.spawn_types.OnBlockSpawnType;
import immersive_particles.core.spawn_types.SpawnType;

import java.util.HashMap;
import java.util.Map;

public class SpawnTypes {
    public static final Map<String, SpawnType> TYPES = new HashMap<>();

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
