package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;

public class WobbleTask extends Task {
    private final WobbleTask.Settings settings;

    public WobbleTask(ImmersiveParticle particle, WobbleTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        particle.setRoll(Math.cos(particle.getAge() * settings.speed) * settings.wobble);

        if (!particle.hasCollided()) {
            // Wobble
            particle.velocityX += Math.cos(particle.getYaw()) * particle.getRoll() * settings.wobble * 0.025;
            particle.velocityZ += Math.sin(particle.getYaw()) * particle.getRoll() * settings.wobble * 0.025;
        }
    }

    public static class Settings extends Task.Settings {
        final double wobble;
        final double speed;

        public Settings(JsonObject settings) {
            wobble = JsonHelper.getDouble(settings, "wobble", 0.1);
            speed = JsonHelper.getDouble(settings, "speed", 1.0);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new WobbleTask(particle, this);
        }
    }
}
