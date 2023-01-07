package immersive_particles.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ImmersiveParticleType {
    private final List<Identifier> textures = new ArrayList<>();
    private List<Sprite> sprites;

    public static ImmersiveParticleType ofJson(JsonObject value) {
        ImmersiveParticleType type = new ImmersiveParticleType();
        for (JsonElement e : value.get("textures").getAsJsonArray()) {
            type.textures.add(new Identifier(e.getAsString()));
        }
        return type;
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
}
