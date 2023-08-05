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
    public void tick(ImmersiveParticle particle) {
        if (particle.hasCollided()) {
            Vector3d target = particle.getTarget();
            if (target != null) {
                particle.setTarget(target.sub(particle.x, particle.y, particle.z).mul(-1).add(particle.x, particle.y, particle.z));
            }

            particle.velocityX *= -1;
            particle.velocityY *= -1;
            particle.velocityZ *= -1;
        }
    }

    public static class Settings extends Task.Settings {
        double avoidPlayerDistance;

        public Settings(JsonObject settings) {
            avoidPlayerDistance = JsonHelper.getDouble(settings, "avoidPlayerDistance", 1.0);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new BounceTask(particle, this);
        }
    }
}
