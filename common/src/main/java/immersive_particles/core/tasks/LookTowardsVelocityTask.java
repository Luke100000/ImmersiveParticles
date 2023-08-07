package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;

public class LookTowardsVelocityTask extends Task {
    private final LookTowardsVelocityTask.Settings settings;

    public LookTowardsVelocityTask(ImmersiveParticle particle, LookTowardsVelocityTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        particle.rotateTowards(particle.getVelocity(), settings.inertia);
    }

    public static class Settings extends Task.Settings {
        float inertia;

        public Settings(JsonObject settings) {
            inertia = JsonHelper.getFloat(settings, "inertia", 0.1f);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new LookTowardsVelocityTask(particle, this);
        }
    }
}
