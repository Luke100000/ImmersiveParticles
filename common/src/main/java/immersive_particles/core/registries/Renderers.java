package immersive_particles.core.registries;

import com.google.gson.JsonObject;
import immersive_particles.core.renderers.FishRenderer;
import immersive_particles.core.renderers.FlappingRenderer;
import immersive_particles.core.renderers.MeshRenderer;
import immersive_particles.core.renderers.ParticleRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Renderers {
    public static final Map<String, Function<JsonObject, ParticleRenderer>> RENDERER = new HashMap<>();

    static {
        register("fish", FishRenderer::new);
        register("flapping", FlappingRenderer::new);
        register("simple", MeshRenderer::new);
    }

    private static void register(String identifier, Function<JsonObject, ParticleRenderer> renderer) {
        RENDERER.put(identifier, renderer);
    }
}
