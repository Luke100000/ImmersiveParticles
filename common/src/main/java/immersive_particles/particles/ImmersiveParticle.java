package immersive_particles.particles;

import immersive_particles.Main;
import immersive_particles.core.ImmersiveParticleManager;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.SpawnLocation;
import immersive_particles.resources.ObjectLoader;
import immersive_particles.util.obj.Face;
import immersive_particles.util.obj.FaceVertex;
import immersive_particles.util.obj.Mesh;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import java.util.List;
import java.util.Random;

public class ImmersiveParticle {
    private static final Box EMPTY_BOUNDING_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private static final double MAX_SQUARED_COLLISION_CHECK_DISTANCE = MathHelper.square(100.0);

    private final Sprite sprite;
    private double x, y, z;
    private double prevPosX, prevPosY, prevPosZ;
    private double velocityX = 0.0;
    private double velocityY = 0.0;
    private double velocityZ = 0.0;
    private int age;
    private final float red, green, blue, alpha;

    private boolean onGround;
    private boolean sticks;

    private Box boundingBox = EMPTY_BOUNDING_BOX;

    protected float velocityMultiplier = 0.98f;

    private float spacingXZ;
    private float spacingY;

    private static final Random random = new Random();

    public ImmersiveParticle(ImmersiveParticleType type, SpawnLocation location) {
        this.sprite = type.getSprites().get(0); //todo random

        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;
        this.alpha = 1.0f;

        this.age = 0;

        this.setBoundingBoxSpacing(0.2f, 0.2f);
        this.setPos(location.x + (random.nextDouble() - 0.5), location.y + (random.nextDouble() - 0.5), location.z + (random.nextDouble() - 0.5));
    }

    public void render(VertexConsumer vertexConsumer, Vec3d camera, float tickDelta) {
        float x = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - camera.getX());
        float y = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - camera.getY());
        float z = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - camera.getZ());

        Matrix4d transform = new Matrix4d();
        transform.rotationXYZ(0.0f, (age + tickDelta) * 0.025f, 0.0f);
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

        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        this.velocityY -= 0.04 * getGravity();
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= this.velocityMultiplier;
        this.velocityY *= this.velocityMultiplier;
        this.velocityZ *= this.velocityMultiplier;
        if (this.onGround) {
            this.velocityX *= 0.7f;
            this.velocityZ *= 0.7f;
        }

        return age > 1000;
    }

    private double getGravity() {
        return 0.1;
    }

    protected int getBrightness() {
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        if (ImmersiveParticleManager.getWorld().isChunkLoaded(blockPos)) {
            return WorldRenderer.getLightmapCoordinates(ImmersiveParticleManager.getWorld(), blockPos);
        }
        return 0;
    }

    public void move(double dx, double dy, double dz) {
        double ox = dx;
        double oy = dy;
        double oz = dz;

        // Collide
        if ((dx != 0.0 || dy != 0.0 || dz != 0.0) && dx * dx + dy * dy + dz * dz < MAX_SQUARED_COLLISION_CHECK_DISTANCE) {
            Vec3d vec3d = Entity.adjustMovementForCollisions(null, new Vec3d(dx, dy, dz), this.getBoundingBox(), ImmersiveParticleManager.getWorld(), List.of());
            dx = vec3d.x;
            dy = vec3d.y;
            dz = vec3d.z;
        }

        // Move bounding box
        if (dx != 0.0 || dy != 0.0 || dz != 0.0) {
            this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        }

        if (Math.abs(oy) >= (double)1.0E-5f && Math.abs(dy) < (double)1.0E-5f) {
            //this.sticks = true;
        }

        if (ox != dx) {
            this.velocityX = 0.0;
        }
        if (oy != dy) {
            this.velocityY = 0.0;
        }
        if (oz != dz) {
            this.velocityZ = 0.0;
        }
    }

    public Box getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(Box box) {
        this.boundingBox = box;

        this.x = (box.minX + box.maxX) / 2.0;
        this.y = box.minY;
        this.z = (box.minZ + box.maxZ) / 2.0;
    }

    protected void setBoundingBoxSpacing(float spacingXZ, float spacingY) {
        if (spacingXZ != this.spacingXZ || spacingY != this.spacingY) {
            this.spacingXZ = spacingXZ;
            this.spacingY = spacingY;
            Box box = this.getBoundingBox();
            double d = (box.minX + box.maxX - (double)spacingXZ) / 2.0;
            double e = (box.minZ + box.maxZ - (double)spacingXZ) / 2.0;
            this.setBoundingBox(new Box(d, box.minY, e, d + (double)this.spacingXZ, box.minY + (double)this.spacingY, e + (double)this.spacingXZ));
        }
    }

    public void setPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        float f = this.spacingXZ / 2.0f;
        float g = this.spacingY;
        this.setBoundingBox(new Box(x - (double)f, y, z - (double)f, x + (double)f, y + (double)g, z + (double)f));
    }
}

