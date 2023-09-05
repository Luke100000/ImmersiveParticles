package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import immersive_particles.util.Utils;
import net.minecraft.util.JsonHelper;

public class WaveTask extends Task {
    private final WaveTask.Settings settings;

    public WaveTask(ImmersiveParticle particle, WaveTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        float time = particle.getAge() * settings.speed;
        float x = Utils.sinNoise(time, 3) - 0.5f;
        float y = Utils.sinNoise(time * 1.1f, 3) - 0.5f;
        float z = Utils.sinNoise(time * 1.2f, 3) - 0.5f;
        particle.velocityX += x * settings.xz * 0.05;
        particle.velocityY += y * settings.y * 0.05;
        particle.velocityZ += z * settings.xz * 0.05;
    }

    public static class Settings extends Task.Settings {
        final float speed;
        final float xz;
        final float y;

        public Settings(JsonObject settings) {
            speed = JsonHelper.getFloat(settings, "speed", 0.025f);
            xz = JsonHelper.getFloat(settings, "xz", 0.025f);
            y = JsonHelper.getFloat(settings, "y", 0.025f);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new WaveTask(particle, this);
        }
    }
}
