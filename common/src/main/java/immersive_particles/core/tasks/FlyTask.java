package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;

public class FlyTask extends MoveTask {
    private final MoveTask.Settings settings;

    public FlyTask(ImmersiveParticle particle, MoveTask.Settings settings) {
        super(particle, settings);

        this.settings = settings;
    }

    @Override
    public void tick(ImmersiveParticle particle) {
        super.tick(particle);

        if (getParticle().getTarget() != null) {
            double dist = Math.sqrt(particle.getSquaredDistanceTo(getParticle().getTarget()));

            // Fly higher
            double up = dist * 0.01; // todo make configurable
            if (particle.velocityY < up) {
                particle.velocityY += up * 0.0025;
            }
        }
    }

    public static class Settings extends MoveTask.Settings {
        final float speed;
        final float acceleration;

        public Settings(JsonObject settings) {
            super(settings);

            speed = JsonHelper.getFloat(settings, "speed", 1.0f);
            acceleration = JsonHelper.getFloat(settings, "acceleration", 0.0f);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new FlyTask(particle, this);
        }
    }
}
