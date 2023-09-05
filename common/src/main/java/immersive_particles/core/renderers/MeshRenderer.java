package immersive_particles.core.renderers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import immersive_particles.Main;
import immersive_particles.core.ImmersiveParticle;
import immersive_particles.resources.ObjectLoader;
import immersive_particles.util.obj.Face;
import immersive_particles.util.obj.FaceVertex;
import immersive_particles.util.obj.Mesh;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.joml.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MeshRenderer extends ParticleRenderer {
    private static final ArrayList<Mesh> EMPTY = new ArrayList<>();
    Mesh DEBUG = ObjectLoader.objects.get(Main.locate("cube")).get("Cube");
    private final HashMap<String, ArrayList<Mesh>> meshes = new HashMap<>();

    final double meshScale;

    public MeshRenderer(JsonObject particle) {
        // Load all meshes
        JsonObject meshDict = JsonHelper.getObject(particle, "meshes");
        for (String state : meshDict.keySet()) {
            ArrayList<Mesh> meshArray = new ArrayList<>();
            for (JsonElement element : meshDict.get(state).getAsJsonArray()) {
                String[] path = element.getAsString().split("\\.");
                meshArray.add(getMesh(Main.locate(path[0]), path[1]));
            }
            meshes.put(state, meshArray);
        }

        if (particle.has("scale")) {
            meshScale = JsonHelper.getDouble(particle, "scale");
        } else {
            meshScale = 1.0 / 16.0;
        }
    }

    @Override
    void render(ImmersiveParticle particle, VertexConsumer vertexConsumer, float tickDelta, Matrix4d transform, Matrix3f normal, Vector4d position) {
        renderMeshes(particle, getMeshes(getCurrentMeshName(particle)), particle.getSprite(), transform, normal, vertexConsumer, tickDelta);
    }

    @Override
    void debugRender(ImmersiveParticle particle, VertexConsumer vertexConsumer, float tickDelta, Vector4d position) {
        if (particle.getTarget() != null && !MinecraftClient.getInstance().gameRenderer.getCamera().isThirdPerson()) {
            Vector3d sub = particle.getTarget().sub(particle.getPosition());

            if (sub.lengthSquared() > 100.0) {
                sub = sub.normalize();
            }

            Matrix4d transform = new Matrix4d();
            transform.setColumn(3, position.add(
                    sub.x,
                    sub.y,
                    sub.z,
                    0.0
            ));
            transform.scale(1.0 / meshScale / 16.0);

            Matrix3f normal = new Matrix3f();

            renderMesh(particle, DEBUG, particle.getSprite(), transform, normal, vertexConsumer, tickDelta);
        }
    }

    List<Mesh> getMeshes(String name) {
        return meshes.getOrDefault(name, EMPTY);
    }

    public String getCurrentMeshName(ImmersiveParticle particle) {
        return "default";
    }

    public Mesh getMesh(Identifier id, String object) {
        if (!ObjectLoader.objects.containsKey(id)) {
            throw new RuntimeException(String.format("Object %s does not exist!", id));
        }
        Mesh mesh = ObjectLoader.objects.get(id).get(object);
        if (mesh == null) {
            throw new RuntimeException(String.format("Mesh %s in %s does not exist!", object, id));
        }
        return mesh;
    }

    Vector4d transformVertex(ImmersiveParticle particle, FaceVertex v, float tickDelta) {
        return new Vector4d(v.v.x * meshScale * particle.scaleX, v.v.y * meshScale * particle.scaleY, v.v.z * meshScale * particle.scaleZ, 1.0f);
    }

    void renderMeshes(ImmersiveParticle particle, List<Mesh> meshes, Sprite sprite, Matrix4d transform, Matrix3f normal, VertexConsumer vertexConsumer, float tickDelta) {
        for (Mesh mesh : meshes) {
            renderMesh(particle, mesh, sprite, transform, normal, vertexConsumer, tickDelta);
        }
    }

    void renderMesh(ImmersiveParticle particle, Mesh mesh, Sprite sprite, Matrix4d transform, Matrix3f normal, VertexConsumer vertexConsumer, float tickDelta) {
        renderMesh(particle, mesh, sprite, transform, normal, vertexConsumer, particle.getLight(), particle.getRed(), particle.getGreen(), particle.getBlue(), particle.getAlpha(), tickDelta);
    }

    void renderMesh(ImmersiveParticle particle, Mesh mesh, Sprite sprite, Matrix4d transform, Matrix3f normal, VertexConsumer vertexConsumer, int light, float r, float g, float b, float a, float tickDelta) {
        for (Face face : mesh.faces) {
            if (face.vertices.size() == 4) {
                for (FaceVertex v : face.vertices) {
                    Vector4d f = transform.transform(transformVertex(particle, v, tickDelta));
                    Vector3f n = normal.transform(new Vector3f(v.n.x, v.n.y, v.n.z));
                    float tu = sprite.getMinU() + (sprite.getMaxU() - sprite.getMinU()) * v.t.u;
                    float tv = sprite.getMinV() + (sprite.getMaxV() - sprite.getMinV()) * v.t.v;
                    vertexConsumer
                            .vertex(f.x, f.y, f.z)
                            .color(r, g, b, a)
                            .texture(tu, tv)
                            .light(light)
                            .normal(n.x, n.y, n.z)
                            .next();
                }
            }
        }
    }
}
