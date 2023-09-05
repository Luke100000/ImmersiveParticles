package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

public class MoveTask extends Task {
    private final MoveTask.Settings settings;

    public MoveTask(ImmersiveParticle particle, MoveTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        Vector3d target = particle.getTarget();
        if (target == null) {
            particle.multiplyVelocity(settings.hoverVelocityMultiplier);
        } else {
            particle.setState(ImmersiveParticle.State.MOVING);
            if (!particle.moveTo(target, settings.speed, settings.acceleration)) {
                particle.multiplyVelocity(settings.hoverVelocityMultiplier);
                particle.setState(ImmersiveParticle.State.IDLE);
            }

            if (particle.getSquaredDistanceTo(target) < settings.reachDistance * settings.reachDistance && particle.getVelocity().lengthSquared() < 0.0001) {
                particle.setTarget(null);
            }
        }
    }

    public static class Settings extends Task.Settings {
        final float speed;
        final float acceleration;
        final double reachDistance;
        final double hoverVelocityMultiplier;

        public Settings(JsonObject settings) {
            speed = JsonHelper.getFloat(settings, "speed", 0.5f);
            acceleration = JsonHelper.getFloat(settings, "acceleration", 0.2f);
            reachDistance = JsonHelper.getDouble(settings, "reachDistance", 0.25);
            hoverVelocityMultiplier = JsonHelper.getDouble(settings, "hoverVelocityMultiplier", 0.8);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new MoveTask(particle, this);
        }
    }
}
