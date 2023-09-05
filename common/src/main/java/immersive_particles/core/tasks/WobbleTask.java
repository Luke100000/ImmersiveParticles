package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import immersive_particles.util.Meth;
import net.minecraft.util.JsonHelper;

public class WobbleTask extends Task {
    private final WobbleTask.Settings settings;

    public WobbleTask(ImmersiveParticle particle, WobbleTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        particle.setRoll(Meth.sin(particle.getAge() * settings.speed) * settings.wobble);

        if (!particle.hasCollided()) {
            // Wobble
            particle.velocityX += Meth.cos(particle.getYaw()) * particle.getRoll() * settings.wobble * 0.025;
            particle.velocityZ += Meth.sin(particle.getYaw()) * particle.getRoll() * settings.wobble * 0.025;
        }
    }

    public static class Settings extends Task.Settings {
        final float wobble;
        final float speed;

        public Settings(JsonObject settings) {
            wobble = JsonHelper.getFloat(settings, "wobble", 0.1f);
            speed = JsonHelper.getFloat(settings, "speed", 1.0f);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new WobbleTask(particle, this);
        }
    }
}
