package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;

public abstract class TargetTask extends Task {
    private final TargetTask.Settings settings;

    private int cooldown;

    public TargetTask(ImmersiveParticle particle, TargetTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        if (settings.researchWhenNoTarget && !particle.hasTarget()) {
            cooldown = 0;
        }

        if (settings.interruptible || particle.getTarget() == null || (particle.getSquaredDistanceTo(particle.getTarget()) < settings.reachDistance * settings.reachDistance) || particle.hasCollided()) {
            cooldown--;
            if (cooldown < 0) {
                cooldown = settings.minCooldown + (int) ((settings.maxCooldown - settings.minCooldown) * particle.getRandom().nextFloat());
                searchTarget();
            }
        }
    }

    protected abstract void searchTarget();

    public static class Settings extends Task.Settings {
        int minCooldown;
        int maxCooldown;
        boolean interruptible;
        double reachDistance;
        boolean researchWhenNoTarget;

        public Settings(JsonObject settings) {
            minCooldown = JsonHelper.getInt(settings, "minCooldown", 10);
            maxCooldown = JsonHelper.getInt(settings, "maxCooldown", 20);
            interruptible = JsonHelper.getBoolean(settings, "incorruptible", true);
            reachDistance = JsonHelper.getDouble(settings, "reachDistance", 0.25);
            researchWhenNoTarget = JsonHelper.getBoolean(settings, "researchWhenNoTarget", false);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            throw new UnsupportedOperationException("TargetTask.Settings.createTask() is not implemented");
        }
    }
}
