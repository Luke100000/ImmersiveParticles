package immersive_particles.core.spawnTypes;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.Searcher;
import immersive_particles.core.SpawnLocation;
import immersive_particles.core.SpawnLocationList;

import java.util.HashMap;
import java.util.Map;

public class OnBlockSpawnType extends FullScanSpawnType {
    public static Map<LookupNode, LookupNode> lookupUpNodes = new HashMap<>();

    public OnBlockSpawnType() {
        super();
    }

    @Override
    public void scanBlock(SpawnLocationList list, Searcher searcher, int x, int y, int z) {
        for (LookupNode node : lookupUpNodes.values()) {
            if (node.validate(searcher, x, y, z)) {
                for (LookupNode value : node.children.values()) {
                    if (value.validate(searcher, x, y - 1, z)) {
                        for (ImmersiveParticleType s : value.types) {
                            list.add(new SpawnLocation(1.0, searcher.cx * 16 + x + 0.5, searcher.cy * 16 + y + 0.5, searcher.cz * 16 + z + 0.5, null, s));
                        }
                    }
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
        LookupNode inNode = LookupNode.fromJson(json.getAsJsonObject("inBlock"));

        // avoid duplicates
        if (lookupUpNodes.containsKey(inNode)) {
            inNode = lookupUpNodes.get(inNode);
        } else {
            lookupUpNodes.put(inNode, inNode);
        }

        LookupNode onNode = LookupNode.fromJson(json.getAsJsonObject("onBlock"));

        // avoid duplicates
        if (inNode.children.containsKey(onNode)) {
            onNode = inNode.children.get(onNode);
        } else {
            inNode.children.put(onNode, onNode);
        }

        // register type
        onNode.types.add(type);
    }
}
