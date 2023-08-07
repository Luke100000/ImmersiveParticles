package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;

public class GlowTask  extends  Task {
    private final GlowTask.Settings settings;

    public GlowTask(ImmersiveParticle particle, GlowTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        double glow = Math.max(0, Math.cos(particle.getAge() * 0.2 * settings.speed) + Math.cos(particle.getAge() * 0.13 * settings.speed) + 0.5);
        particle.setGlow(glow);
    }

    public static class Settings extends Task.Settings {
        double speed;

        public Settings(JsonObject settings) {
            speed = JsonHelper.getDouble(settings, "speed", 1.0);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new GlowTask(particle, this);
        }
    }
}
