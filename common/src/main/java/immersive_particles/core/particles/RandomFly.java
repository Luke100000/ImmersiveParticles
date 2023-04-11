package immersive_particles.core.particles;

import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.searcher.SpawnLocation;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

public class RandomFly extends FlyingParticle {
    private Vector3d direction;
    private final Vector3d origin;

    private final float speed;
    private final float acceleration;
    private final float inertia;
    private final float range;
    private final float flyingTime;

    private int flying;

    public RandomFly(ImmersiveParticleType type, SpawnLocation location, ImmersiveParticle leader) {
        super(type, location, leader);

        speed = JsonHelper.getFloat(type.behavior, "speed", 0.5f);
        acceleration = JsonHelper.getFloat(type.behavior, "acceleration", 0.1f);
        inertia = JsonHelper.getFloat(type.behavior, "inertia", 0.025f);
        range = JsonHelper.getFloat(type.behavior, "range", 5.0f);
        flyingTime = JsonHelper.getInt(type.behavior, "flyingTime", 30);

        origin = new Vector3d(x, y, z);

        nextTarget();
    }

    @Override
    public boolean tick() {
        if (shouldUpdate()) {
            flying--;

            if (flying < 0) {
                nextTarget();
            }

            // Move to the target
            moveTowards(direction,  speed, acceleration);

            // Bounce
            if (collided) {
                direction.mul(-1);
                velocityX *= -1;
                velocityY *= -1;
                velocityZ *= -1;
            }

            // Look to target
            rotateTowards(getVelocity(), inertia);

            // Panic
            if (avoidPlayer()) {
                nextTarget();
            }
        }

        return super.tick();
    }

    private void nextTarget() {
        flying = (int)(flyingTime * (random.nextFloat() + 0.75f));

        direction = new Vector3d(
                random.nextDouble() - 0.5,
                random.nextDouble() - 0.5,
                random.nextDouble() - 0.5
        ).mul(range).sub(x, y, z).add(origin).normalize();
    }
}
