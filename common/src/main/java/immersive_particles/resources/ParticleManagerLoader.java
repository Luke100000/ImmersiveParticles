package immersive_particles.resources;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import immersive_particles.Main;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.registries.SpawnTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerSystem;

import java.util.*;

@Environment(value = EnvType.CLIENT)
public class ParticleManagerLoader extends JsonDataLoader {
    protected static final Identifier ID = Main.locate("immersive_particles");

    public static final Map<Identifier, ImmersiveParticleType> PARTICLES = new HashMap<>();

    public static final Identifier ATLAS_TEXTURE = Main.locate("textures/atlas/immersive_particles.png");

    public ParticleManagerLoader() {
        super(new GsonBuilder().create(), ID.getPath());
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        SpawnTypes.clear();

        SpriteAtlasTexture atlasTexture = new SpriteAtlasTexture(ATLAS_TEXTURE);
        MinecraftClient.getInstance().getTextureManager().registerTexture(atlasTexture.getId(), atlasTexture);

        // Parse particle types
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            try {
                PARTICLES.put(entry.getKey(), new ImmersiveParticleType(entry.getValue().getAsJsonObject()));
            } catch (JsonSyntaxException exception) {
                Main.LOGGER.error("Failed to parse particle " + entry.getKey() + ": " + exception.getMessage());
            }
        }

        // List all textures used
        Set<Identifier> textures = new HashSet<>();
        for (ImmersiveParticleType type : PARTICLES.values()) {
            textures.addAll(type.getTextures());
        }

        // Create a texture atlas
        MinecraftClient.getInstance().execute(() -> {
            // Dummy profiler
            ProfilerSystem profilerSystem = new ProfilerSystem(Util.nanoTimeSupplier, () -> 0, false);

            profilerSystem.startTick();
            SpriteAtlasTexture.Data data = atlasTexture.stitch(manager, textures.stream(), profilerSystem, 0);
            profilerSystem.endTick();

            atlasTexture.upload(data);

            // Give particle types their sprites
            for (ImmersiveParticleType type : PARTICLES.values()) {
                List<Sprite> sprites = type.getTextures().stream().map(atlasTexture::getSprite).toList();
                type.setSprites(sprites);
            }
        });
    }
}

