package immersive_particles.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import immersive_particles.Main;
import immersive_particles.core.registries.Renderers;
import immersive_particles.core.registries.SpawnTypes;
import immersive_particles.core.registries.Tasks;
import immersive_particles.core.renderers.ParticleRenderer;
import immersive_particles.core.tasks.Task;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ImmersiveParticleType {
    private final List<Identifier> textures = new ArrayList<>();
    private List<Sprite> sprites;

    ParticleRenderer renderer;
    List<Task.Settings> taskSettings = new LinkedList<>();

    int minCount;
    int maxCount;

    // Some generic particle settings
    float velocityMultiplier;

    public ImmersiveParticleType(JsonObject value) {
        minCount = JsonHelper.getInt(value, "minCount", 1);
        maxCount = JsonHelper.getInt(value, "maxCount", 1);

        velocityMultiplier = JsonHelper.getFloat(value, "velocityMultiplier", 0.98f);

        // Load textures
        for (JsonElement e : value.get("textures").getAsJsonArray()) {
            textures.add(new Identifier(e.getAsString()));
        }

        // Load renderer
        JsonObject rendererSettings = JsonHelper.getObject(value, "renderer");
        String rendererName = JsonHelper.getString(rendererSettings, "type", "simple");
        if (Renderers.RENDERER.containsKey(rendererName)) {
            renderer = Renderers.RENDERER.get(rendererName).apply(rendererSettings);
        } else {
            Main.LOGGER.error("Unknown renderer type: " + rendererName);
        }

        // Load task settings
        for (JsonElement task : JsonHelper.getArray(value, "tasks")) {
            String taskName = JsonHelper.getString(task.getAsJsonObject(), "type");
            if (Tasks.TASKS.containsKey(taskName)) {
                taskSettings.add(Tasks.TASKS.get(taskName).apply(task.getAsJsonObject()));
            } else {
                Main.LOGGER.error("Unknown task type: " + taskName);
            }
        }

        // Register types to the spawn types
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

    public float getVelocityMultiplier() {
        return velocityMultiplier;
    }

    public ParticleRenderer getRenderer() {
        return renderer;
    }

    public List<Task.Settings> getTaskSettings() {
        return taskSettings;
    }
}
