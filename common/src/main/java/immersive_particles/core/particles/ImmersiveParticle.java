package immersive_particles.core.particles;

import immersive_particles.core.ImmersiveParticleManager;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.SpawnLocation;
import immersive_particles.resources.ObjectLoader;
import immersive_particles.util.Utils;
import immersive_particles.util.obj.Face;
import immersive_particles.util.obj.FaceVertex;
import immersive_particles.util.obj.Mesh;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.*;

import java.lang.Math;
import java.util.List;
import java.util.Random;

public abstract class ImmersiveParticle {
    private static final Box EMPTY_BOUNDING_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private static final double MAX_SQUARED_COLLISION_CHECK_DISTANCE = MathHelper.square(100.0);

    public boolean visible;
    double x, y, z;
    double prevPosX, prevPosY, prevPosZ;
    double velocityX = 0.0;
    double velocityY = 0.0;
    double velocityZ = 0.0;

    double yaw = random.nextDouble() * Math.PI * 2.0;
    double prevYaw = 0.0;
    double pitch = 0.0;
    double prevPitch = 0.0;
    double roll = 0.0;
    double prevRoll = 0.0;

    int age, maxAge;
    final float red, green, blue, alpha;
    int light;

    boolean onGround;
    boolean collided;
    boolean sticks;

    private Box boundingBox = EMPTY_BOUNDING_BOX;

    protected float velocityMultiplier = 0.98f;

    float spacingXZ;
    float spacingY;

    float avoidPlayerDistance;

    static final Random random = new Random();

    ImmersiveParticle leader;

    public ImmersiveParticle(ImmersiveParticleType type, SpawnLocation location) {
        this.red = 1.0f;
        this.green = 1.0f;
        this.blue = 1.0f;
        this.alpha = 1.0f;

        this.age = 0;
        this.maxAge = 1000; //todo

        this.setBoundingBoxSpacing(0.2f, 0.2f);
        this.setPos(getRandomPosition(location));

        avoidPlayerDistance = JsonHelper.getFloat(type.behavior, "avoidPlayer", 1.0f);
    }

    Vector3d getRandomPosition(SpawnLocation location) {
        return location.getRandomPosition(spacingXZ, spacingY, spacingXZ);
    }

    public void render(VertexConsumer vertexConsumer, Vec3d camera, float tickDelta) {
        double x = MathHelper.lerp(tickDelta, this.prevPosX, this.x) - camera.getX();
        double y = MathHelper.lerp(tickDelta, this.prevPosY, this.y) - camera.getY();
        double z = MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - camera.getZ();

        double yaw = MathHelper.lerp(tickDelta, this.prevYaw, this.yaw);
        double pitch = MathHelper.lerp(tickDelta, this.prevPitch, this.pitch);
        double roll = MathHelper.lerp(tickDelta, this.prevRoll, this.roll);

        Matrix4d transform = new Matrix4d();
        transform.rotationZYX(roll, yaw, pitch);
        float a = Math.min(maxAge, age + tickDelta);
        float transition = 0.1f;
        transform.scale(Math.min(1.0f, Math.min(a * transition, (maxAge - a) * transition)));
        transform.setColumn(3, new Vector4d(x, y, z, 1.0));

        Matrix3f normal = new Matrix3f();
        normal.rotationZYX((float)roll, (float)yaw, (float)pitch);

        render(vertexConsumer, tickDelta, transform, normal);
    }

    abstract void render(VertexConsumer vertexConsumer, float tickDelta, Matrix4d transform, Matrix3f normal);

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

    void renderObject(Mesh mesh, Sprite sprite, Matrix4d transform, Matrix3f normal, VertexConsumer vertexConsumer, float tickDelta) {
        renderObject(mesh, sprite, transform, normal, vertexConsumer, light, red, green, blue, alpha, tickDelta);
    }

    Vector4d transformVertex(FaceVertex v, float tickDelta) {
        return new Vector4d(v.v.x / 16.0f, v.v.y / 16.0f, v.v.z / 16.0f, 1.0f);
    }

    void renderObject(Mesh mesh, Sprite sprite, Matrix4d transform, Matrix3f normal, VertexConsumer vertexConsumer, int light, float r, float g, float b, float a, float tickDelta) {
        for (Face face : mesh.faces) {
            if (face.vertices.size() == 4) {
                for (FaceVertex v : face.vertices) {
                    Vector4d f = transform.transform(transformVertex(v, tickDelta));
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

    public boolean tick() {
        age++;

        if (visible) {
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

            light = this.getBrightness();
        }

        return age > maxAge;
    }

    double getGravity() {
        return 0.0;
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
            // todo adjustMovement is by far the slowest part, replace by cached solid block check for particles far away
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

        this.onGround = oy != dy && dy < 0;
        this.collided = false;

        if (ox != dx) {
            this.velocityX = 0.0;
            this.collided = true;
        }
        if (oy != dy) {
            this.velocityY = 0.0;
            this.collided = true;
        }
        if (oz != dz) {
            this.velocityZ = 0.0;
            this.collided = true;
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

    public void setPos(Vector3d pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        float f = this.spacingXZ / 2.0f;
        float g = this.spacingY;
        this.setBoundingBox(new Box(x - (double)f, y, z - (double)f, x + (double)f, y + (double)g, z + (double)f));
    }

    public void setLeader(ImmersiveParticle leader) {
        this.leader = leader;
    }

    protected double getSquaredDistanceTo(ImmersiveParticle particle) {
        return Utils.squaredDistance(x, particle.x, y, particle.y, z, particle.z);
    }

    protected double getSquaredDistanceTo(Vector3d pos) {
        return Utils.squaredDistance(x, pos.x, y, pos.y, z, pos.z);
    }

    protected void moveTo(Vector3d target, float speed) {
        moveTo(target, Math.sqrt(getSquaredDistanceTo(target)), speed);
    }

    protected void moveTo(Vector3d target, double distance, float speed) {
        // Move to the target
        double minDistance = 0.0625;
        if (distance > minDistance) {
            double f = Math.min(1.0, distance - minDistance) * speed / distance;
            velocityX = velocityX + (target.x - x) * f;
            velocityY = velocityY + (target.y - y) * f;
            velocityZ = velocityZ + (target.z - z) * f;
        }
    }

    protected void rotateToTarget(Vector3d target, float rotReact) {
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.prevRoll = this.roll;

        // Rotate
        yaw = yaw * (1 - rotReact) + Math.atan2(target.x - x, target.z - z) * rotReact;
        pitch = pitch * (1 - rotReact) - Math.atan((target.y - y) / Math.sqrt(Math.pow(target.x - x, 2.0) + Math.pow(target.z - z, 2.0))) * rotReact;
        roll = roll * (1 - rotReact) + Math.cos(age * 0.25) * 0.5 * rotReact;
    }

    protected void avoidPlayer() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            Vec3d pos = player.getPos();
            double v = Utils.squaredDistance(pos.x, x, pos.y, y, pos.z, z) + 0.01;
            if (v < avoidPlayerDistance * (1.0 + player.getVelocity().length() * 20.0)) {
                double speed = 0.1 / v;
                velocityX += (x - pos.x) * speed;
                velocityY += (y - pos.y) * speed;
                velocityZ += 0.02 * speed;
            }
        }
    }
}

