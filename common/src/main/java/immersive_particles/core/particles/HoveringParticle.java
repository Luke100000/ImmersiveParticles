package immersive_particles.core.particles;

import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.ImmersiveParticlesChunkManager;
import immersive_particles.core.SpawnLocation;
import immersive_particles.util.Utils;
import net.minecraft.util.math.Vec3d;

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

    @Override
    public boolean tick() {
        double dist = Math.sqrt(Utils.squaredDistance(x, target.x, y, target.y, z, target.z)) - 0.1;
        if (dist < 0.0f) {
            hovering--;
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
        pitch = pitch * (1 - rotReact) + Math.atan((target.y - y) / Math.sqrt(Math.pow(target.x - x, 2.0) + Math.pow(target.z - z, 2.0))) * rotReact;
        roll = roll * (1 - rotReact) + Math.cos(age * 0.25) * 0.5 * rotReact;

        // Wobble
        velocityX += Math.cos(yaw) * roll * 0.025;
        velocityZ += Math.sin(yaw) * roll * 0.025;

        // Fly higher
        double up = dist * 0.01;
        if (velocityY < up) {
            velocityY += up * 0.05;
        }

        return super.tick();
    }
}
