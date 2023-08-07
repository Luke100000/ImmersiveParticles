package immersive_particles.core.registries;

import com.google.gson.JsonObject;
import immersive_particles.core.tasks.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Tasks {
    public static final Map<String, Function<JsonObject, Task.Settings>> TASKS = new HashMap<>();

    static {
        register("avoidAir", AvoidAirTask.Settings::new);
        register("avoidPlayerByTarget", AvoidPlayerByTargetTask.Settings::new);
        register("avoidPlayer", AvoidPlayerTask.Settings::new);
        register("avoidWater", AvoidWaterTask.Settings::new);
        register("batter", BatterTask.Settings::new);
        register("bounce", BounceTask.Settings::new);
        register("fly", FlyTask.Settings::new);
        register("followPlayer", FollowPlayer.Settings::new);
        register("followLeader", FollowLeaderTask.Settings::new);
        register("glow", GlowTask.Settings::new);
        register("lookTowardsTarget", LookTowardsTargetTask.Settings::new);
        register("lookTowardsVelocity", LookTowardsVelocityTask.Settings::new);
        register("move", MoveTask.Settings::new);
        register("push", PushTask.Settings::new);
        register("randomSpawnTarget", RandomSpawnTargetTask.Settings::new);
        register("randomTarget", RandomTargetTask.Settings::new);
        register("roadWalker", RoadWalkerTask.Settings::new);
        register("stomped", StompedTask.Settings::new);
        register("walk", WalkTask.Settings::new);
        register("wobble", WobbleTask.Settings::new);
    }

    private static void register(String identifier, Function<JsonObject, Task.Settings> type) {
        TASKS.put(identifier, type);
    }
}
