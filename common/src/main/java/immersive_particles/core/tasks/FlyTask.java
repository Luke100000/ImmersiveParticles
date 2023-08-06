package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;

public class FlyTask extends MoveTask {
    private final FlyTask.Settings settings;

    public FlyTask(ImmersiveParticle particle, FlyTask.Settings settings) {
        super(particle, settings);

        this.settings = settings;
    }

    @Override
    public void tick() {
        super.tick();

        if (particle.getTarget() != null && settings.maxUpwardsSpeed > 10.0) {
            double dist = Math.sqrt(particle.getSquaredDistanceTo(particle.getTarget()));

            // Fly higher
            double up = dist * settings.maxUpwardsSpeed; // todo make configurable
            if (particle.velocityY < up) {
                particle.velocityY += up * settings.upwardsAcceleration;
            }
        }
    }

    public static class Settings extends MoveTask.Settings {
        final double maxUpwardsSpeed;
        final double upwardsAcceleration;

        public Settings(JsonObject settings) {
            super(settings);

            maxUpwardsSpeed = JsonHelper.getDouble(settings, "maxUpwardsSpeed", 0.0);
            upwardsAcceleration = JsonHelper.getDouble(settings, "upwardsAcceleration", 0.0025);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new FlyTask(particle, this);
        }
    }
}
