package immersive_particles.particles;

import immersive_particles.Main;
import immersive_particles.core.ImmersiveParticleManager;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.SpawnLocation;
import immersive_particles.resources.ObjectLoader;
import immersive_particles.util.obj.Face;
import immersive_particles.util.obj.FaceVertex;
import immersive_particles.util.obj.Mesh;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

public class ImmersiveParticle {
    private final Sprite sprite;
    private final double x, y, z;
    private final double prevPosX, prevPosY, prevPosZ;
    private int age;
    private final float red, green, blue, alpha;

    public ImmersiveParticle(ImmersiveParticleType type, SpawnLocation location) {
        this.sprite = type.getSprites().get(0); //todo random

        this.x = location.x;
        this.y = location.y;
        this.z = location.z;

        this.prevPosX = location.x;
        this.prevPosY = location.y;
        this.prevPosZ = location.z;

        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;
        this.alpha = 1.0f;

        this.age = 0;
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d cam = camera.getPos();
        float x = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - cam.getX());
        float y = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - cam.getY());
        float z = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - cam.getZ());

        Matrix4d transform = new Matrix4d();
        transform.rotationXYZ(0.0f, (age + tickDelta) * 0.25f, 0.0f);
        transform.scale(Math.min(1.0f, (age + tickDelta) * 0.1));
        transform.setColumn(3, new Vector4d(x, y, z, 1.0));

        Matrix3d normal = transform.get3x3(new Matrix3d());

        int light = this.getBrightness();
        Mesh mesh = getMesh(Main.locate("bumblebee"), "Cube");
        renderObject(mesh, transform, normal, vertexConsumer, light);
    }

    public Mesh getMesh(Identifier id, String object) {
        if (!ObjectLoader.objects.containsKey(id)) {
            throw new RuntimeException(String.format("Object %s does not exist!", id));
        }
        Mesh mesh = ObjectLoader.objects.get(id).get(object);
        if (mesh == null) {
            throw new RuntimeException(String.format("Mesh %s in %s does not exist!", id, object));
        }
        return mesh;
    }

    void renderObject(Mesh mesh, Matrix4d transform, Matrix3d normal, VertexConsumer vertexConsumer, int light) {
        renderObject(mesh, transform, normal, vertexConsumer, light, red, green, blue, alpha);
    }

    void renderObject(Mesh mesh, Matrix4d transform, Matrix3d normal, VertexConsumer vertexConsumer, int light, float r, float g, float b, float a) {
        for (Face face : mesh.faces) {
            if (face.vertices.size() == 4) {
                for (FaceVertex v : face.vertices) {
                    Vector3d f_t = normal.transform(new Vector3d(v.v.x / 16.0f, v.v.y / 16.0f, v.v.z / 16.0f));
                    Vector4d f = transform.transform(new Vector4d(f_t.x, f_t.y, f_t.z, 1.0));
                    //Vector3d n = normal.transform(new Vector3d(v.n.x, v.n.y, v.n.z));
                    //todo light
                    float tu = sprite.getMinU() + (sprite.getMaxU() - sprite.getMinU()) * v.t.u;
                    float tv = sprite.getMinV() + (sprite.getMaxV() - sprite.getMinV()) * v.t.v;
                    vertexConsumer
                            .vertex(f.x, f.y, f.z)
                            .texture(tu, tv)
                            .color(r, g, b, a)
                            .light(light)
                            .next();
                }
            }
        }
    }

    public boolean tick() {
        age++;
        return age > 1000;
    }

    protected int getBrightness() {
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        if (ImmersiveParticleManager.getWorld().isChunkLoaded(blockPos)) {
            return WorldRenderer.getLightmapCoordinates(ImmersiveParticleManager.getWorld(), blockPos);
        }
        return 0;
    }
}

