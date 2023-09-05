package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

import java.util.Random;

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
            double distance;
            if (particle.getTarget() != null) {
                distance = leader.getSquaredDistanceTo(particle.getTarget());
            } else {
                distance = leader.getSquaredDistanceTo(particle.getPosition());
            }
            if (distance > settings.maxDistance * settings.maxDistance) {
                double f = (settings.maxDistance - settings.minDistance) / 2.0;
                Random random = particle.getRandom();
                Vector3d leaderToParticle = particle.getPosition().sub(leader.getPosition()).normalize();
                Vector3d target = leaderToParticle.mul(settings.minDistance + f).add(leader.getPosition()).add(
                        (random.nextDouble() - 0.5) * f,
                        (random.nextDouble() - 0.5) * f,
                        (random.nextDouble() - 0.5) * f
                );
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
