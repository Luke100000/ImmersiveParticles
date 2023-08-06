package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

public class RandomTargetTask extends Task {
    private final RandomTargetTask.Settings settings;

    private final Vector3d origin;

    private int cooldown;

    public RandomTargetTask(ImmersiveParticle particle, RandomTargetTask.Settings settings) {
        super(particle);
        this.settings = settings;

        origin = new Vector3d(particle.getX(), particle.getY(), particle.getZ());
    }

    @Override
    public void tick() {
        if (settings.interruptible || !particle.hasTarget() || particle.hasCollided()) {
            cooldown--;
            if (cooldown < 0) {
                cooldown = settings.minCooldown + (int) ((settings.maxCooldown - settings.minCooldown) * (particle.getRandom().nextFloat() + 0.75f));

                Vector3d direction = new Vector3d(
                        particle.getRandom().nextDouble() - 0.5,
                        particle.getRandom().nextDouble() - 0.5,
                        particle.getRandom().nextDouble() - 0.5
                ).mul(settings.rangeXZ, settings.rangeY, settings.rangeXZ).sub(particle.x, particle.y, particle.z).add(origin);

                particle.setTarget(direction.add(particle.x, particle.y, particle.z));
            }
        }
    }

    public static class Settings extends Task.Settings {
        int minCooldown;
        int maxCooldown;
        boolean interruptible;
        double rangeXZ;
        double rangeY;
        double reachDistance;

        public Settings(JsonObject settings) {
            minCooldown = JsonHelper.getInt(settings, "minCooldown", 10);
            maxCooldown = JsonHelper.getInt(settings, "maxCooldown", 20);
            interruptible = JsonHelper.getBoolean(settings, "incorruptible", true);
            rangeXZ = JsonHelper.getDouble(settings, "rangeXZ", 5.0);
            rangeY = JsonHelper.getDouble(settings, "rangeY", 3.0);
            reachDistance = JsonHelper.getDouble(settings, "reachDistance", 0.25);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new RandomTargetTask(particle, this);
        }
    }
}
