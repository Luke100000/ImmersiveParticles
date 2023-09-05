package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

public class FlapTask extends Task {
    private final FlapTask.Settings settings;
    private int tick;
    private int cooldown;

    public FlapTask(ImmersiveParticle particle, FlapTask.Settings settings) {
        super(particle);

        this.settings = settings;
    }

    @Override
    public void tick() {
        cooldown--;

        if (cooldown < 0) {
            cooldown = settings.minCooldown + particle.getRandom().nextInt(settings.maxCooldown - settings.minCooldown);

            Vector3d target = particle.getTarget();
            double strength = (target == null ? 1.0 : Math.min(1.0, target.distanceSquared(particle.getPosition()))) * settings.strength;

            Vector3d direction = new Vector3d(
                    (particle.getRandom().nextDouble() - 0.5) * settings.randomize,
                    (particle.getRandom().nextDouble() - 0.5) * settings.randomize - 1.0,
                    (particle.getRandom().nextDouble() - 0.5) * settings.randomize
            ).normalize().mul(strength * (tick % 2 == 0 ? 1.0 : -1.0));

            tick++;

            particle.velocityX += direction.x;
            particle.velocityY += direction.y;
            particle.velocityZ += direction.z;
        }
    }

    public static class Settings extends Task.Settings {
        final double randomize;
        final double strength;
        final int minCooldown;
        final int maxCooldown;

        public Settings(JsonObject settings) {
            randomize = JsonHelper.getDouble(settings, "randomize", 0.1);
            strength = JsonHelper.getDouble(settings, "strength", 0.1);
            minCooldown = JsonHelper.getInt(settings, "minCooldown", 10);
            maxCooldown = JsonHelper.getInt(settings, "maxCooldown", 30);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new FlapTask(particle, this);
        }
    }
}
