package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

public class LookTowardsTargetTask extends Task {
    private final LookTowardsTargetTask.Settings settings;

    public LookTowardsTargetTask(ImmersiveParticle particle, LookTowardsTargetTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        Vector3d target = particle.getTarget();
        if (target != null) {
            if (settings.noY) {
                particle.rotateToTarget(new Vector3d(target.x, particle.y, target.z), settings.inertia);
            } else {
                particle.rotateToTarget(target, settings.inertia);
            }
        }
    }

    public static class Settings extends Task.Settings {
        float inertia;
        boolean noY;

        public Settings(JsonObject settings) {
            inertia = JsonHelper.getFloat(settings, "inertia", 0.1f);
            noY = JsonHelper.getBoolean(settings, "noY", false);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new LookTowardsTargetTask(particle, this);
        }
    }
}
