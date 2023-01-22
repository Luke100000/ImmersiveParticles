package immersive_particles.core.particles;

import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.ImmersiveParticlesChunkManager;
import immersive_particles.core.SpawnLocation;
import immersive_particles.util.obj.FaceVertex;
import org.joml.Vector3d;
import org.joml.Vector4d;

import java.util.List;

public class HoveringParticle extends SimpleParticle {
    private static final int HOVER_TIME = 20;

    private final List<SpawnLocation> targets;
    private Vector3d target;
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
        if (visible) {
            double dist = Math.sqrt(getSquaredDistanceTo(target));
            if (dist < 0.125f || collided) {
                hovering -= (collided ? 10 : 1);
                if (hovering < 0 && targets.size() > 0) {
                    target = getRandomPosition(targets.get(random.nextInt(targets.size())));
                    hovering = (int)(HOVER_TIME * (random.nextFloat() + 0.75f));
                }
            }

            // Move to the target
            moveTo(target, dist, 0.01f);

            // Look to target
            rotateToTarget(target, 0.1f);

            // Wobble
            velocityX += Math.cos(yaw) * roll * 0.025;
            velocityZ += Math.sin(yaw) * roll * 0.025;

            // Fly higher
            double up = dist * 0.01;
            if (velocityY < up) {
                velocityY += up * 0.0025;
            }

            // Panic
            avoidPlayer();
        }

        return super.tick();
    }
}
