package immersive_particles.core.particles;

import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.searcher.ParticleChunkManager;
import immersive_particles.core.searcher.SpawnLocation;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

import java.util.List;

public class HoveringParticle extends FlyingParticle {
    private final List<SpawnLocation> targets;
    private Vector3d target;

    private final int hoveringTime;
    private final int landingTime;
    private final float speed;
    private final float acceleration;
    private final float inertia;
    private final float wobble;
    private final float wobbleSpeed;

    private int hovering;
    private int landing;

    public HoveringParticle(ImmersiveParticleType type, SpawnLocation location, ImmersiveParticle leader) {
        super(type, location, leader);

        targets = ParticleChunkManager.getClose(type, location.x, location.y, location.z);
        target = getRandomPosition(location);

        hoveringTime = JsonHelper.getInt(type.behavior, "hoveringTime", 20);
        landingTime = JsonHelper.getInt(type.behavior, "landingTime", 20);
        speed = JsonHelper.getFloat(type.behavior, "speed", 0.01f);
        acceleration = JsonHelper.getFloat(type.behavior, "acceleration", 0.01f);
        inertia = JsonHelper.getFloat(type.behavior, "inertia", 0.01f);
        wobble = JsonHelper.getFloat(type.behavior, "wobble", 0.0f);
        wobbleSpeed = JsonHelper.getFloat(type.behavior, "wobbleSpeed", 0.0f);

        unrest();
    }

    @Override
    public boolean tick() {
        if (shouldUpdate()) {
            double dist = Math.sqrt(getSquaredDistanceTo(target));
            if (dist < 0.125f || collided) {
                hovering -= (collided ? 10 : 1);
                if (hovering < 0) {
                    landing -= 1;
                    setCurrentMesh("sit");
                    if (landing < 0 && targets.size() > 0) {
                        target = getRandomPosition(targets.get(random.nextInt(targets.size())));
                        unrest();

                        //todo sometimes, choose an air target and don't land
                    }
                }
            }

            if (hovering > 0) {
                // Move to the target
                moveTo(target, dist, speed, acceleration);

                // Look to target
                rotateToTarget(target, inertia);

                if (wobble > 0.0) {
                    // roll side to side
                    setRoll(Math.cos(age * wobbleSpeed) * wobble);

                    // Wobble
                    velocityX += Math.cos(getYaw()) * getRoll() * wobble * 0.025;
                    velocityZ += Math.sin(getYaw()) * getRoll() * wobble * 0.025;
                }

                // Fly higher
                double up = dist * 0.01;
                if (velocityY < up) {
                    velocityY += up * 0.0025;
                }
            }

            // Panic
            if (avoidPlayer()) {
                unrest();
            }
        }

        return super.tick();
    }

    private void unrest() {
        landing = (int)(landingTime * (random.nextFloat() + 0.75f));
        hovering = (int)(hoveringTime * (random.nextFloat() + 0.75f));
    }
}
