package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class RoadWalkerTask extends Task {
    private static final String MEMORY = "RoadWalkerTask_targets";

    private final RoadWalkerTask.Settings settings;

    private final List<Vector3d> targets;
    private int targetIndex;
    private int targetIndexDirection = 1;
    private int walking;

    public RoadWalkerTask(ImmersiveParticle particle, RoadWalkerTask.Settings settings) {
        super(particle);

        this.settings = settings;

        if (particle.getLeader() == null) {
            targets = new ArrayList<>();
            particle.memory.put(MEMORY, targets);
            findTarget();
        } else {
            //todo massive unsafe
            //noinspection unchecked
            targets = (List<Vector3d>) particle.getLeader().memory.get(MEMORY);
            particle.setTarget(new Vector3d(particle.x, particle.y, particle.z));
        }
    }

    private void findTarget() {
        addTarget();
        particle.setTarget(new Vector3d(particle.x + (particle.getRandom().nextDouble() - 0.5) * settings.initialRange, particle.y + (particle.getRandom().nextDouble() - 0.5) * settings.initialRange, particle.z + (particle.getRandom().nextDouble() - 0.5) * settings.initialRange));
    }

    private void addTarget() {
        if (targets.isEmpty()) {
            targets.add(particle.getPosition());
        } else {
            Vector3d d = targets.get(targets.size() - 1);
            if (particle.getSquaredDistanceTo(d) > 0.05) {
                targets.add(particle.getPosition());
            }
        }
    }

    private void mutateTarget() {
        addTarget();
        Vector3d target = particle.getTarget();
        if (target != null) {
            target.add((particle.getRandom().nextDouble()) * settings.randomizerRange, (particle.getRandom().nextDouble()) * settings.randomizerRange, (particle.getRandom().nextDouble()) * settings.randomizerRange);
            particle.setTarget(target);
        }
    }

    @Override
    public void tick() {
        // Find new target
        Vector3d target = particle.getTarget();
        if (target != null && particle.getSquaredDistanceTo(target) < 0.1 || !particle.isMoving()) {
            if (particle.hasLeader() && targets.size() > 1) {
                // Follow the path the leader has chosen
                targetIndex += targetIndexDirection;
                if (targetIndex >= targets.size()) {
                    targetIndexDirection = -1;
                    targetIndex += targetIndexDirection;
                }
                if (targetIndex < 1) {
                    targetIndexDirection = 1;
                    targetIndex += targetIndexDirection;
                }
                particle.setTarget(targets.get(targetIndex));
            } else {
                // Randomize the target
                findTarget();
            }
        }

        // Mutate the position to walk more randomly
        if (!particle.hasLeader()) {
            walking--;
            if (walking < 0) {
                mutateTarget();
                walking = (int) (settings.walkingTime * (particle.getRandom().nextFloat() + 0.75f));
            }
        }

        // Path has been explored, join the swarm
        if (!particle.hasLeader() && targets.size() > settings.roadLength) {
            particle.setLeader(particle);
        }
    }

    public static class Settings extends Task.Settings {
        private final int roadLength;
        private final int walkingTime;
        private final float randomizerRange;
        private final float initialRange;

        public Settings(JsonObject settings) {
            roadLength = JsonHelper.getInt(settings, "roadLength", 10);
            walkingTime = JsonHelper.getInt(settings, "walkingTime", 100);
            randomizerRange = JsonHelper.getFloat(settings, "randomizerRange", 1.0f);
            initialRange = JsonHelper.getFloat(settings, "initialRange", 10.0f);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new RoadWalkerTask(particle, this);
        }
    }
}
