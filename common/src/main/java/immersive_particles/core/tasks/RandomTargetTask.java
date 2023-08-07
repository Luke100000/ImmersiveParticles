package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

public class RandomTargetTask extends TargetTask {
    private final RandomTargetTask.Settings settings;

    private final Vector3d origin;

    public RandomTargetTask(ImmersiveParticle particle, RandomTargetTask.Settings settings) {
        super(particle, settings);

        this.settings = settings;

        origin = new Vector3d(particle.getX(), particle.getY(), particle.getZ());
    }

    @Override
    protected void searchTarget() {
        Vector3d direction = new Vector3d(
                particle.getRandom().nextDouble() - 0.5,
                particle.getRandom().nextDouble() - 0.5,
                particle.getRandom().nextDouble() - 0.5
        ).mul(settings.rangeXZ, settings.rangeY, settings.rangeXZ).sub(particle.x, particle.y, particle.z).add(origin);

        particle.setTarget(direction.add(particle.x, particle.y, particle.z));
    }

    public static class Settings extends TargetTask.Settings {
        double rangeXZ;
        double rangeY;

        public Settings(JsonObject settings) {
            super(settings);

            rangeXZ = JsonHelper.getDouble(settings, "rangeXZ", 5.0);
            rangeY = JsonHelper.getDouble(settings, "rangeY", 3.0);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new RandomTargetTask(particle, this);
        }
    }
}
