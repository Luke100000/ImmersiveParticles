package immersive_particles.core.searcher;

import immersive_particles.core.ImmersiveParticleType;

import java.util.*;

public class SpawnLocationList {
    private final Map<ImmersiveParticleType, List<SpawnLocation>> locations = new HashMap<>();
    private double totalChance = 0.0f;

    public void add(ImmersiveParticleType type, SpawnLocation location) {
        getLocations(type).add(location);
        totalChance += location.chance;
    }

    public List<SpawnLocation> getLocations(ImmersiveParticleType type) {
        return locations.computeIfAbsent(type, k -> new LinkedList<>());
    }

    public Map<ImmersiveParticleType, List<SpawnLocation>> getAllLocations() {
        return locations;
    }

    public double getTotalChance() {
        return totalChance;
    }

    public void shuffle() {
        locations.values().forEach(Collections::shuffle);
    }
}
