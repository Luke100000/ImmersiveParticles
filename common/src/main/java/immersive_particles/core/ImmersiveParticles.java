package immersive_particles.core;

import immersive_particles.core.particles.HoveringParticle;
import immersive_particles.core.particles.ImmersiveParticle;
import immersive_particles.core.particles.SwimmingSwarmParticle;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ImmersiveParticles {
    static public final Map<String, BiFunction<ImmersiveParticleType, SpawnLocation, ImmersiveParticle>> PARTICLES = new HashMap<>();

    static {
        register("hovering", HoveringParticle::new);
        register("swimmingSwarm", SwimmingSwarmParticle::new);
    }

    private static void register(String identifier, BiFunction<ImmersiveParticleType, SpawnLocation, ImmersiveParticle> type) {
        PARTICLES.put(identifier, type);
    }
}
