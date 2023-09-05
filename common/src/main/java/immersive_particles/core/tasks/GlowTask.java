package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import immersive_particles.util.Meth;
import net.minecraft.util.JsonHelper;

public class GlowTask  extends  Task {
    private final GlowTask.Settings settings;

    public GlowTask(ImmersiveParticle particle, GlowTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        float glow = Math.max(0, Meth.sin(particle.getAge() * 0.2f * settings.speed) + Meth.sin(particle.getAge() * 0.13f * settings.speed) + 0.5f);
        particle.setGlow(glow);
    }

    public static class Settings extends Task.Settings {
        float speed;

        public Settings(JsonObject settings) {
            speed = JsonHelper.getFloat(settings, "speed", 1.0f);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new GlowTask(particle, this);
        }
    }
}
