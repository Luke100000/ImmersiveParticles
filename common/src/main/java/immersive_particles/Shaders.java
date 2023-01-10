package immersive_particles;

import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;

public class Shaders {
    public static Shader IMMERSIVE_PARTICLE_CUTOUT;

    public static void LoadShaders(ResourceManager manager) {
        IMMERSIVE_PARTICLE_CUTOUT = loadShader(manager, "immersive_particle_cutout", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
    }

    private static Shader loadShader(ResourceFactory factory, String name, VertexFormat vertexFormat) {
        try {
            return new Shader(factory, name, vertexFormat);
        }
        catch (Exception exception) {
            throw new IllegalStateException("Could not load immersive particles shader " + name, exception);
        }
    }
}
