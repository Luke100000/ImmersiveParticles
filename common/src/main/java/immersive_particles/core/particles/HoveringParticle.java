package immersive_particles.core.particles;

import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.ImmersiveParticlesChunkManager;
import immersive_particles.core.SpawnLocation;
import immersive_particles.util.Utils;
import immersive_particles.util.obj.FaceVertex;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4d;

import java.util.List;

public class HoveringParticle extends SimpleParticle {
    private static final int HOVER_TIME = 20;

    private final List<SpawnLocation> targets;
    private Vec3d target;
    private int hovering = (int)(HOVER_TIME * (random.nextFloat() + 0.75f));

    public HoveringParticle(ImmersiveParticleType type, SpawnLocation location) {
        super(type, location);

        targets = ImmersiveParticlesChunkManager.getClose(type, location.x, location.y, location.z);
        target = getRandomPosition(location);
    }

    double getFlap(float tickDelta) {
        return org.joml.Math.cos((age + tickDelta) * 2.0) * 0.5;
    }

    @Override
    Vector4d transformVertex(FaceVertex v, float tickDelta) {
        double flap = getFlap(tickDelta);
        double ox = v.c.r > 0 ? v.c.r * (org.joml.Math.cos(flap) - 1) * v.v.x : 0;
        double oy = v.c.r > 0 ? v.c.r * org.joml.Math.sin(flap) * Math.abs(v.v.x) : 0;
        return new Vector4d((v.v.x + ox) / 16.0f, (v.v.y + oy) / 16.0f, v.v.z / 16.0f, 1.0f);
    }

    @Override
    public boolean tick() {
        double dist = Math.sqrt(Utils.squaredDistance(x, target.x, y, target.y, z, target.z)) - 0.1;
        if (dist < 0.0f || collided) {
            hovering -= (collided ? 10 : 1);
            if (hovering < 0 && targets.size() > 0) {
                target = getRandomPosition(targets.get(random.nextInt(targets.size())));
                hovering = (int)(HOVER_TIME * (random.nextFloat() + 0.75f));
            }
        }

        // Move to the target
        double react = 0.1;
        if (dist > 0.0) {
            double speed = Math.min(1.0, dist) * 0.1;
            velocityX = velocityX * (1.0 - react) - react * (x - target.x) / dist * speed;
            velocityY = velocityY * (1.0 - react) - react * (y - target.y) / dist * speed;
            velocityZ = velocityZ * (1.0 - react) - react * (z - target.z) / dist * speed;
        }

        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.prevRoll = this.roll;

        // Rotate
        float rotReact = 0.1f;
        yaw = yaw * (1 - rotReact) + Math.atan2(target.x - x, target.z - z) * rotReact;
        pitch = pitch * (1 - rotReact) - Math.atan((target.y - y) / Math.sqrt(Math.pow(target.x - x, 2.0) + Math.pow(target.z - z, 2.0))) * rotReact;
        roll = roll * (1 - rotReact) + Math.cos(age * 0.25) * 0.5 * rotReact;

        // Wobble
        velocityX += Math.cos(yaw) * roll * 0.025;
        velocityZ += Math.sin(yaw) * roll * 0.025;

        // Fly higher
        double up = dist * 0.01;
        if (velocityY < up) {
            velocityY += up * 0.0025;
        }

        // Panic
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            Vec3d pos = player.getPos();
            double v = Utils.squaredDistance(pos.x, x, pos.y, y, pos.z, z) + 0.01;
            double panicDistance = 1.0 + player.getVelocity().length() * 20.0;
            if (v < panicDistance) {
                double speed = 0.1 / v;
                velocityX += (x - pos.x) * speed;
                velocityY += (y - pos.y) * speed;
                velocityZ += 0.02 * speed;
            }
        }

        return super.tick();
    }
}
