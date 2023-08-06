package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;

public class StompedTask extends Task {
    private final StompedTask.Settings settings;

    public StompedTask(ImmersiveParticle particle, StompedTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        if (particle.isTouchingPlayer()) {
            particle.setState(ImmersiveParticle.State.DEAD);
        }
    }

    public static class Settings extends Task.Settings {
        public Settings(JsonObject settings) {

        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new StompedTask(particle, this);
        }
    }
}
