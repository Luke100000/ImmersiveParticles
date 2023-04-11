package immersive_particles.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import immersive_particles.core.registries.SpawnTypes;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.List;

public class ImmersiveParticleType {
    private final List<Identifier> textures = new ArrayList<>();
    private List<Sprite> sprites;

    public JsonObject behavior;
    String behaviorIdentifier;

    int minCount;
    int maxCount;

    public ImmersiveParticleType(JsonObject value) {
        for (JsonElement e : value.get("textures").getAsJsonArray()) {
            textures.add(new Identifier(e.getAsString()));
        }

        behavior = JsonHelper.getObject(value, "behavior");
        behaviorIdentifier = JsonHelper.getString(behavior, "type");

        minCount = JsonHelper.getInt(value, "minCount", 1);
        maxCount = JsonHelper.getInt(value, "maxCount", 1);

        // Register types to the spawn types
        // todo often its helpful to merge the first object, and then only modify in other spawns
        for (JsonElement e : value.get("spawns").getAsJsonArray()) {
            SpawnTypes.registerParticleType(e.getAsJsonObject(), this);
        }
    }

    public List<Identifier> getTextures() {
        return textures;
    }

    public void setSprites(List<Sprite> sprites) {
        this.sprites = sprites;
    }

    public List<Sprite> getSprites() {
        return sprites;
    }

    public int getMinCount() {
        return minCount;
    }

    public int getMaxCount() {
        return maxCount;
    }
}
