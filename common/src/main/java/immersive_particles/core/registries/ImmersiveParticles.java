package immersive_particles.core.registries;

import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.searcher.SpawnLocation;
import immersive_particles.core.particles.*;
import org.apache.commons.lang3.function.TriFunction;

import java.util.HashMap;
import java.util.Map;

public class ImmersiveParticles {
    static public final Map<String, TriFunction<ImmersiveParticleType, SpawnLocation, ImmersiveParticle, ImmersiveParticle>> PARTICLES = new HashMap<>();

    static {
        register("hovering", HoveringParticle::new);
        register("swimmingSwarm", SwimmingSwarmParticle::new);
        register("crawling", CrawlingParticle::new);
        register("jellyfish", JellyfishParticle::new);
        register("randomFly", RandomFly::new);
        register("fireFly", FireFlyParticle::new);
    }

    private static void register(String identifier, TriFunction<ImmersiveParticleType, SpawnLocation, ImmersiveParticle, ImmersiveParticle> type) {
        PARTICLES.put(identifier, type);
    }
}
