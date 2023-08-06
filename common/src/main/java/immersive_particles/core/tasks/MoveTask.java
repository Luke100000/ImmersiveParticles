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
        if (target != null) {
            particle.moveTo(target, settings.speed, settings.acceleration);

            if (particle.getSquaredDistanceTo(target) < settings.reachDistance * settings.reachDistance) {
                particle.setTarget(null);
            }
        }
    }

    public static class Settings extends Task.Settings {
        final float speed;
        final float acceleration;
        final boolean stopOnTarget;
        final double reachDistance;

        public Settings(JsonObject settings) {
            speed = JsonHelper.getFloat(settings, "speed", 0.5f);
            acceleration = JsonHelper.getFloat(settings, "acceleration", 0.2f);
            stopOnTarget = JsonHelper.getBoolean(settings, "stopOnTarget", false);
            reachDistance = JsonHelper.getDouble(settings, "reachDistance", 0.25);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new MoveTask(particle, this);
        }
    }
}
