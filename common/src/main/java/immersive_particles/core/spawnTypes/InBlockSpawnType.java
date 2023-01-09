package immersive_particles.core.spawnTypes;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.Searcher;
import immersive_particles.core.SpawnLocation;
import immersive_particles.core.SpawnLocationList;

import java.util.HashMap;
import java.util.Map;

public class InBlockSpawnType extends FullScanSpawnType {
    public static Map<LookupNode, LookupNode> lookupUpNodes = new HashMap<>();

    public InBlockSpawnType() {
        super();
    }

    @Override
    public void scanBlock(SpawnLocationList list, Searcher searcher, int x, int y, int z) {
        for (LookupNode node : lookupUpNodes.values()) {
            if (node.validate(searcher, x, y, z)) {
                for (ImmersiveParticleType s : node.types) {
                    list.add(new SpawnLocation(1.0, searcher.cx * 16 + x + 0.5, searcher.cy * 16 + y + 0.5, searcher.cz * 16 + z + 0.5, null, s));
                }
            }
        }
    }

    @Override
    public void clear() {
        lookupUpNodes.clear();
    }

    @Override
    public void register(JsonObject json, ImmersiveParticleType type) {
        LookupNode node = LookupNode.fromJson(json.getAsJsonObject("block"));

        // avoid duplicates
        if (lookupUpNodes.containsKey(node)) {
            node = lookupUpNodes.get(node);
        } else {
            lookupUpNodes.put(node, node);
        }

        // register type
        node.types.add(type);
    }
}
