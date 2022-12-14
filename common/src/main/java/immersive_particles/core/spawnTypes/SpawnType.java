package immersive_particles.core.spawnTypes;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.Searcher;
import immersive_particles.core.SpawnLocationList;


public abstract class SpawnType {
    public SpawnType() {

    }

    public void setWorld() {
        //todo reset any world acceleration data
    }

    public void clear() {

    }

    public abstract void register(JsonObject json, ImmersiveParticleType type);

    public abstract void scan(SpawnLocationList list, Searcher searcher);
}
