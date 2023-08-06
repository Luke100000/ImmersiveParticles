package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;

public class WalkTask extends MoveTask {
    private final WalkTask.Settings settings;

    public WalkTask(ImmersiveParticle particle, WalkTask.Settings settings) {
        super(particle, settings);

        this.settings = settings;
    }

    @Override
    public void tick() {
        if (particle.hasCollided()) {
            particle.moveTo(particle.getTarget(), settings.speed, settings.acceleration);
        }
    }

    public static class Settings extends MoveTask.Settings {
        public Settings(JsonObject settings) {
            super(settings);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new WalkTask(particle, this);
        }
    }
}
