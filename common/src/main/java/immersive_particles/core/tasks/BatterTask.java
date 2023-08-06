package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;

public class BatterTask extends Task {
    private final BatterTask.Settings settings;

    public BatterTask(ImmersiveParticle particle, BatterTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        if (particle.getImpactY() > settings.fatalHeight) {
            particle.setState(ImmersiveParticle.State.DEAD);
        }
    }

    public static class Settings extends Task.Settings {
        final double fatalHeight;

        public Settings(JsonObject settings) {
            fatalHeight = JsonHelper.getDouble(settings, "fatalHeight", 0.0);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new BatterTask(particle, this);
        }
    }
}
