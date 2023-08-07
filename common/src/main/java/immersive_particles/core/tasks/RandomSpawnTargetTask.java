package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import immersive_particles.core.searcher.ParticleChunkManager;
import immersive_particles.core.searcher.SpawnLocation;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

import java.util.List;

public class RandomSpawnTargetTask extends TargetTask {
    private final List<SpawnLocation> targets;

    Vector3d getRandomPosition(SpawnLocation location) {
        return location.getRandomPosition(particle.getSpacingXZ(), particle.getSpacingY(), particle.getSpacingXZ());
    }

    public RandomSpawnTargetTask(ImmersiveParticle particle, RandomSpawnTargetTask.Settings settings) {
        super(particle, settings);

        SpawnLocation location = particle.getSpawnLocation();
        targets = ParticleChunkManager.getClose(particle.getType(), location.x, location.y, location.z, settings.distance, settings.maxTargets);

        particle.setTarget(getRandomPosition(location));
    }

    @Override
    protected void searchTarget() {
        if (!targets.isEmpty()) {
            particle.setTarget(getRandomPosition(targets.get(particle.getRandom().nextInt(targets.size()))));
        }
    }

    public static class Settings extends TargetTask.Settings {
        double distance;
        int maxTargets;

        public Settings(JsonObject settings) {
            super(settings);

            distance = JsonHelper.getDouble(settings, "distance", 10);
            maxTargets = JsonHelper.getInt(settings, "maxTargets", 5);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new RandomSpawnTargetTask(particle, this);
        }
    }
}
