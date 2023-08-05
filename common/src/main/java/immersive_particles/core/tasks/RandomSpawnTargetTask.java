package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import immersive_particles.core.searcher.ParticleChunkManager;
import immersive_particles.core.searcher.SpawnLocation;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

import java.util.List;

public class RandomSpawnTargetTask extends Task {
    private final ImmersiveParticle particle;
    private final RandomSpawnTargetTask.Settings settings;

    private final List<SpawnLocation> targets;

    private int cooldown;

    Vector3d getRandomPosition(SpawnLocation location) {
        return location.getRandomPosition(particle.getSpacingXZ(), particle.getSpacingY(), particle.getSpacingXZ());
    }

    public RandomSpawnTargetTask(ImmersiveParticle particle, RandomSpawnTargetTask.Settings settings) {
        super(particle);
        this.particle = particle;
        this.settings = settings;

        SpawnLocation location = particle.getSpawnLocation();
        targets = ParticleChunkManager.getClose(particle.getType(), location.x, location.y, location.z, settings.distance, settings.maxTargets);

        particle.setTarget(getRandomPosition(location));
    }

    @Override
    public void tick(ImmersiveParticle particle) {
        if (settings.interruptible || particle.hasTarget()) {
            cooldown--;
            if (cooldown < 0) {
                cooldown = settings.minCooldown + (int) ((settings.maxCooldown - settings.minCooldown) * (particle.getRandom().nextFloat() + 0.75f));

                particle.setTarget(getRandomPosition(targets.get(particle.getRandom().nextInt(targets.size()))));
            }
        }
    }

    public static class Settings extends Task.Settings {
        int minCooldown;
        int maxCooldown;
        boolean interruptible;
        double distance;
        int maxTargets;

        public Settings(JsonObject settings) {
            minCooldown = JsonHelper.getInt(settings, "minCooldown", 10);
            maxCooldown = JsonHelper.getInt(settings, "maxCooldown", 20);
            interruptible = JsonHelper.getBoolean(settings, "incorruptible", false);
            distance = JsonHelper.getDouble(settings, "distance", 10.0);
            maxTargets = JsonHelper.getInt(settings, "maxTargets", 10);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new RandomSpawnTargetTask(particle, this);
        }
    }
}
