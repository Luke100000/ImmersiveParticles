package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

public class MoveTask extends Task{
    private final MoveTask.Settings settings;

    public MoveTask(ImmersiveParticle particle, MoveTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick(ImmersiveParticle particle) {
        Vector3d target = particle.getTarget();
        if (target != null) {
            getParticle().moveTo(target, settings.speed, settings.acceleration);
        }
    }

    public static class Settings extends Task.Settings {
        final float speed;
        final float acceleration;

        public Settings(JsonObject settings) {
            speed = JsonHelper.getFloat(settings, "speed", 1.0f);
            acceleration = JsonHelper.getFloat(settings, "acceleration", 0.0f);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new MoveTask(particle, this);
        }
    }
}
