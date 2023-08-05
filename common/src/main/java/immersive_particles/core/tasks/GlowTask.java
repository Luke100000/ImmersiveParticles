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
    public void tick(ImmersiveParticle particle) {
        double glow = Math.max(0, Math.cos(particle.getAge() * 0.33) + Math.cos(particle.getAge() * 0.17) + 0.5);
        particle.setGlow(glow);
    }

    public static class Settings extends Task.Settings {
        double avoidPlayerDistance;

        public Settings(JsonObject settings) {
            avoidPlayerDistance = JsonHelper.getDouble(settings, "avoidPlayerDistance", 1.0);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new GlowTask(particle, this);
        }
    }
}
