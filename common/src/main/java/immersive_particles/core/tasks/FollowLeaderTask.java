package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

public class FollowLeaderTask extends Task {
    private final FollowLeaderTask.Settings settings;

    public FollowLeaderTask(ImmersiveParticle particle, FollowLeaderTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        ImmersiveParticle leader = particle.getLeader();
        if (leader != null) {
            double distance = leader.getSquaredDistanceTo(particle.getPosition());
            if (distance > settings.maxDistance * settings.maxDistance) {
                Vector3d target = leader.getPosition().sub(particle.getPosition()).normalize().mul(settings.minDistance).add(particle.getPosition());
                particle.setTarget(target);
            }
        }
    }

    public static class Settings extends Task.Settings {
        double minDistance;
        double maxDistance;

        public Settings(JsonObject settings) {
            minDistance = JsonHelper.getDouble(settings, "minDistance", 2);
            maxDistance = JsonHelper.getDouble(settings, "maxDistance", 5);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new FollowLeaderTask(particle, this);
        }
    }
}
