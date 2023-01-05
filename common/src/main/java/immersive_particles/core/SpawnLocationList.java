package immersive_particles.core;

import java.util.LinkedList;
import java.util.List;

public class SpawnLocationList {
    private final List<SpawnLocation> locations = new LinkedList<>();
    private long lastAccess;
    private double totalChance = 0.0f;

    public void add(SpawnLocation location) {
        locations.add(location);
        totalChance += location.chance;
    }

    public List<SpawnLocation> getLocations() {
        return locations;
    }

    public long getLastAccess() {
        return lastAccess;
    }

    public double getTotalChance() {
        return totalChance;
    }
}
