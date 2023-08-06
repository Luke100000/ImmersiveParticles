package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

public class BounceTask extends Task {
    private final BounceTask.Settings settings;

    public BounceTask(ImmersiveParticle particle, BounceTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        if (particle.hasCollided()) {
            Vector3d target = particle.getTarget();
            if (target != null) {
                particle.setTarget(target.sub(particle.x, particle.y, particle.z).mul(-1).add(particle.x, particle.y, particle.z));
            }

            particle.velocityX *= -settings.bounce;
            particle.velocityY *= -settings.bounce;
            particle.velocityZ *= -settings.bounce;
        }
    }

    public static class Settings extends Task.Settings {
        double avoidPlayerDistance;
        double bounce;

        public Settings(JsonObject settings) {
            avoidPlayerDistance = JsonHelper.getDouble(settings, "avoidPlayerDistance", 1.0);
            bounce = JsonHelper.getDouble(settings, "bounce", 1.0);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new BounceTask(particle, this);
        }
    }
}
