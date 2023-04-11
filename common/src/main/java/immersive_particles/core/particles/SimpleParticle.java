package immersive_particles.core.particles;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import immersive_particles.Main;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.SpawnLocation;
import immersive_particles.util.obj.Mesh;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.JsonHelper;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleParticle extends ImmersiveParticle {
    private static final ArrayList<Mesh> EMPTY = new ArrayList<>();
    private final Sprite sprite;
    private final HashMap<String, ArrayList<Mesh>> meshes = new HashMap<>();

    private String currentMesh = "default";

    public SimpleParticle(ImmersiveParticleType type, SpawnLocation location, ImmersiveParticle leader) {
        super(type, location, leader);

        this.sprite = type.getSprites().get(random.nextInt(type.getSprites().size()));

        // Load all meshes
        JsonObject meshDict = JsonHelper.getObject(type.behavior, "meshes");
        for (String state : meshDict.keySet()) {
            ArrayList<Mesh> meshArray = new ArrayList<>();
            for (JsonElement element : meshDict.get(state).getAsJsonArray()) {
                String[] path = element.getAsString().split("\\.");
                meshArray.add(getMesh(Main.locate(path[0]), path[1]));
            }
            meshes.put(state, meshArray);
        }
    }

    @Override
    void render(VertexConsumer vertexConsumer, float tickDelta, Matrix4d transform, Matrix3f normal, Vector4d position) {
        renderObject(getCurrentMeshes(), getCurrentSprite(), transform, normal, vertexConsumer, tickDelta);
    }

    List<Mesh> getMeshes(String name) {
        return meshes.getOrDefault(name, EMPTY);
    }

    List<Mesh> getCurrentMeshes() {
        return getMeshes(currentMesh);
    }

    public String getCurrentMesh() {
        return currentMesh;
    }

    public void setCurrentMesh(String currentMesh) {
        this.currentMesh = currentMesh;
    }

    Sprite getCurrentSprite() {
        return sprite;
    }
}
