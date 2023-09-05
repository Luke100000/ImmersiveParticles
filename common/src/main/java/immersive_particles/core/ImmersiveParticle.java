package immersive_particles.core;

import immersive_particles.Config;
import immersive_particles.core.searcher.SpawnLocation;
import immersive_particles.core.tasks.Task;
import immersive_particles.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.*;

public class ImmersiveParticle {
    private static final Box EMPTY_BOUNDING_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private static final double MAX_SQUARED_COLLISION_CHECK_DISTANCE = MathHelper.square(100.0);

    private final ImmersiveParticleType type;
    private final SpawnLocation location;

    private final Sprite sprite;

    public boolean visible;
    public double x, y, z;
    double prevPosX, prevPosY, prevPosZ;
    public double velocityX = 0.0;
    public double velocityY = 0.0;
    public double velocityZ = 0.0;

    float yaw = 0.0f;
    float prevYaw = 0.0f;
    float pitch = 0.0f;
    float prevPitch = 0.0f;
    float roll = 0.0f;
    float prevRoll = 0.0f;

    public double scaleX = 1.0;
    public double scaleY = 1.0;
    public double scaleZ = 1.0;

    public double gravity = 0.0;

    int age, maxAge;
    float red = 1.0f;
    float green = 1.0f;
    float blue = 1.0f;
    float alpha = 1.0f;
    int light;
    double glow;

    public Map<String, Object> memory = new HashMap<>();

    State state = State.MOVING;

    @Nullable
    Vector3d target = null;

    public Vector3d getPosition() {
        return new Vector3d(x, y, z);
    }

    public enum State {
        MOVING,
        IDLE,
        FALLING,
        DEAD
    }

    boolean onGround;
    public boolean collided;

    float velocityMultiplier;

    Box boundingBox = EMPTY_BOUNDING_BOX;
    float spacingXZ;
    float spacingY;

    double distanceFallen;
    double impactY;

    final Random random = new Random();

    ImmersiveParticle leader;

    List<Task> tasks;

    public ImmersiveParticle(ImmersiveParticleType type, SpawnLocation location, ImmersiveParticle leader) {
        this.type = type;
        this.location = location;

        this.sprite = type.getSprites().get(random.nextInt(type.getSprites().size()));

        ImmersiveParticleType.Color color = type.colors.get(random.nextInt(type.colors.size()));
        this.red = color.red;
        this.green = color.green;
        this.blue = color.blue;

        this.age = random.nextInt(20);
        this.maxAge = Config.getInstance().particleMaxAge + random.nextInt(60); //todo

        this.yaw = (float) (random.nextFloat() * Math.PI * 2.0f);

        this.velocityMultiplier = type.getVelocityMultiplier();

        this.setBoundingBoxSpacing(0.2f, 0.2f);
        this.setPos(getRandomPosition(location));

        this.leader = leader;

        // Construct tasks
        tasks = new LinkedList<>();
        for (Task.Settings settings : type.getTaskSettings()) {
            tasks.add(settings.createTask(this));
        }
    }

    Vector3d getRandomPosition(SpawnLocation location) {
        return location.getRandomPosition(spacingXZ, spacingY, spacingXZ);
    }

    public boolean tick() {
        age++;

        if (shouldUpdate()) {
            this.prevPosX = this.x;
            this.prevPosY = this.y;
            this.prevPosZ = this.z;
            this.velocityY -= 0.04 * getGravity();

            if (state == State.DEAD) {
                velocityX = 0.0;
                velocityZ = 0.0;
                setYaw(getYaw());
                setPitch(0.0);
                setRoll(0.0);
            } else {
                for (Task task : tasks) {
                    task.tick();
                }
            }

            this.move(this.velocityX, this.velocityY, this.velocityZ);

            multiplyVelocity(this.velocityMultiplier);

            if (this.onGround) {
                this.velocityX *= 0.7f;
                this.velocityZ *= 0.7f;
            }

            light = this.getBrightness();
        }

        return age > maxAge;
    }

    public void multiplyVelocity(double velocityMultiplier) {
        this.velocityX *= velocityMultiplier;
        this.velocityY *= velocityMultiplier;
        this.velocityZ *= velocityMultiplier;
    }

    double getGravity() {
        return gravity;
    }

    protected int getRawBrightness() {
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        if (ImmersiveParticleManager.getWorld().isChunkLoaded(blockPos)) {
            return WorldRenderer.getLightmapCoordinates(ImmersiveParticleManager.getWorld(), blockPos);
        }
        return 0;
    }

    protected int getBrightness() {
        int light = getRawBrightness();
        int blockLight = Math.min(15, LightmapTextureManager.getBlockLightCoordinates(light) + (int) (getGlow() * 16));
        return LightmapTextureManager.pack(
                blockLight,
                LightmapTextureManager.getSkyLightCoordinates(light)
        );
    }

    private static Vec3d adjustMovementForCollisions(double vx, double vy, double vz, Box entityBoundingBox, List<VoxelShape> collisions) {
        if (collisions.isEmpty()) {
            return new Vec3d(vx, vy, vz);
        }
        if (vy != 0.0 && (vy = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entityBoundingBox, collisions, vy)) != 0.0) {
            entityBoundingBox = entityBoundingBox.offset(0.0, vy, 0.0);
        }
        boolean bl = Math.abs(vx) < Math.abs(vz);
        if (bl && vz != 0.0 && (vz = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions, vz)) != 0.0) {
            entityBoundingBox = entityBoundingBox.offset(0.0, 0.0, vz);
        }
        if (vx != 0.0) {
            vx = VoxelShapes.calculateMaxOffset(Direction.Axis.X, entityBoundingBox, collisions, vx);
            if (!bl && vx != 0.0) {
                entityBoundingBox = entityBoundingBox.offset(vx, 0.0, 0.0);
            }
        }
        if (!bl && vz != 0.0) {
            vz = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions, vz);
        }
        return new Vec3d(vx, vy, vz);
    }

    public static Vec3d adjustMovementForCollisions(double vx, double vy, double vz, Box entityBoundingBox, World world) {
        Iterable<VoxelShape> collisions = world.getBlockCollisions(null, entityBoundingBox.stretch(vx, vy, vz));
        List<VoxelShape> shapes = Lists.newArrayList(collisions.iterator());
        return adjustMovementForCollisions(vx, vy, vz, entityBoundingBox, shapes);
    }

    public void move(double dx, double dy, double dz) {
        double ox = dx;
        double oy = dy;
        double oz = dz;

        // Collide
        if ((dx != 0.0 || dy != 0.0 || dz != 0.0) && dx * dx + dy * dy + dz * dz < MAX_SQUARED_COLLISION_CHECK_DISTANCE) {
            // todo adjustMovement is by far the slowest part, replace by cached solid block check for particles far away
            Vec3d vec3d = adjustMovementForCollisions(dx, dy, dz, this.getBoundingBox(), ImmersiveParticleManager.getWorld());
            dx = vec3d.x;
            dy = vec3d.y;
            dz = vec3d.z;
        }

        // Move bounding box
        if (dx != 0.0 || dy != 0.0 || dz != 0.0) {
            this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        }

        this.onGround = oy != dy && dy < 0;
        this.collided = false;

        if (onGround) {
            impactY = distanceFallen;
            distanceFallen = 0.0;
        } else {
            impactY = 0.0f;
            this.distanceFallen += dy;
        }

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
            double d = (box.minX + box.maxX - (double) spacingXZ) / 2.0;
            double e = (box.minZ + box.maxZ - (double) spacingXZ) / 2.0;
            this.setBoundingBox(new Box(d, box.minY, e, d + (double) this.spacingXZ, box.minY + (double) this.spacingY, e + (double) this.spacingXZ));
        }
    }

    public void setPos(Vector3d pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        float f = this.spacingXZ / 2.0f;
        float g = this.spacingY;
        this.setBoundingBox(new Box(x - (double) f, y, z - (double) f, x + (double) f, y + (double) g, z + (double) f));
    }

    protected double getSquaredDistanceTo(ImmersiveParticle particle) {
        return Utils.squaredDistance(x, particle.x, y, particle.y, z, particle.z);
    }

    public double getSquaredDistanceTo(Vector3d pos) {
        return Utils.squaredDistance(x, pos.x, y, pos.y, z, pos.z);
    }

    public boolean moveTo(Vector3d target, float speed, float acceleration) {
        return moveTo(target, Math.sqrt(getSquaredDistanceTo(target)), speed, acceleration);
    }

    public boolean moveTo(Vector3d target, double distance, float speed, float acceleration) {
        float currentSpeed = (float) getVelocity().length();
        speed *= acceleration * Math.max(0.0f, speed - currentSpeed);

        // Move to the target
        double minDistance = 0.25;
        if (distance > minDistance) {
            double length = getVelocity().length();
            double f = Math.min(1.0, distance - minDistance) * Math.max(0.0, speed - length) / distance;
            velocityX = velocityX + (target.x - x) * f;
            velocityY = velocityY + (target.y - y) * f;
            velocityZ = velocityZ + (target.z - z) * f;
            return true;
        } else {
            return false;
        }
    }

    public void moveTowards(Vector3d direction, float speed, float acceleration) {
        float currentSpeed = (float) getVelocity().length();
        moveTowards(direction, acceleration * Math.max(0.0f, speed - currentSpeed));
    }

    public void moveTowards(Vector3d direction, float speed) {
        velocityX = velocityX + direction.x * speed;
        velocityY = velocityY + direction.y * speed;
        velocityZ = velocityZ + direction.z * speed;
    }

    public void rotateToTarget(Vector3d target, float rotReact) {
        Vector3d direction = new Vector3d(target).sub(x, y, z);
        if (direction.lengthSquared() > 0.125f * 0.125f) {
            rotateTowards(direction, rotReact);
        }
    }

    public Vector3d getVelocity() {
        return new Vector3d(velocityX, velocityY, velocityZ);
    }

    public void setYaw(double yaw) {
        setYaw((float) yaw);
    }

    public void setPitch(double yaw) {
        setPitch((float) yaw);
    }

    public void setRoll(double yaw) {
        setRoll((float) yaw);
    }

    public void setYaw(float yaw) {
        this.prevYaw = this.yaw;
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.prevPitch = this.pitch;
        this.pitch = pitch;
    }

    public void setRoll(float roll) {
        this.prevRoll = this.roll;
        this.roll = roll;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getRoll() {
        return roll;
    }

    public double getYaw(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevYaw, this.yaw);
    }

    public double getPitch(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevPitch, this.pitch);
    }

    public double getRoll(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevRoll, this.roll);
    }

    public void rotateTowards(Vector3d direction, float rotReact) {
        float l = (float) Math.sqrt(Math.pow(direction.x, 2.0) + Math.pow(direction.z, 2.0));
        if (l > 0.00001f) {
            setYaw(yaw * (1.0f - rotReact) + Math.atan2(direction.x, direction.z) * rotReact);
            setPitch(pitch * (1.0f - rotReact) - Math.atan((direction.y) / l) * rotReact);
        } else {
            if (direction.y > 0.0) {
                setPitch(-Math.PI / 2);
            } else {
                setPitch(Math.PI / 2);
            }
        }
    }

    public boolean shouldUpdate() {
        return visible;
    }

    public boolean hasLeader() {
        return leader != null;
    }

    public void setLeader(ImmersiveParticle leader) {
        this.leader = leader;
    }

    public boolean isVisible() {
        return visible;
    }

    public double getX() {
        return x;
    }

    public double getX(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevPosX, x);
    }

    public double getY() {
        return y;
    }

    public double getY(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevPosY, y);
    }

    public double getZ() {
        return z;
    }

    public double getZ(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevPosZ, z);
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public double getVelocityZ() {
        return velocityZ;
    }

    public int getAge() {
        return age;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public float getAlpha() {
        return alpha;
    }

    public int getLight() {
        return light;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public boolean hasCollided() {
        return collided;
    }

    public float getVelocityMultiplier() {
        return velocityMultiplier;
    }

    public float getSpacingXZ() {
        return spacingXZ;
    }

    public float getSpacingY() {
        return spacingY;
    }

    public double getDistanceFallen() {
        return distanceFallen;
    }

    public double getImpactY() {
        return impactY;
    }

    @Nullable
    public ImmersiveParticle getLeader() {
        return leader;
    }

    public Random getRandom() {
        return random;
    }

    public ImmersiveParticleType getType() {
        return type;
    }

    public SpawnLocation getSpawnLocation() {
        return location;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public @Nullable Vector3d getTarget() {
        return target == null ? null : new Vector3d(target);
    }

    public boolean hasTarget() {
        return target != null;
    }

    public void setTarget(@Nullable Vector3d target) {
        this.target = target;
    }

    public double getGlow() {
        return glow;
    }

    public void setGlow(double glow) {
        this.glow = glow;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void render(BufferBuilder builder, Vec3d camera, float tickDelta) {
        getType().getRenderer().render(this, builder, camera, tickDelta);
    }

    public boolean isTouchingPlayer() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return player != null && player.getBoundingBox().intersects(getBoundingBox());
    }

    public boolean isMoving() {
        double dx = x - prevPosX;
        double dy = y - prevPosY;
        double dz = z - prevPosZ;
        return (dx * dx + dy * dy + dz * dz) > 0.0001;
    }
}

