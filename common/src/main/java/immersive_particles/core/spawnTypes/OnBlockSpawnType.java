package immersive_particles.core.spawnTypes;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.Searcher;
import immersive_particles.core.SpawnLocation;
import immersive_particles.core.SpawnLocationList;
import net.minecraft.block.BlockState;

import java.util.HashMap;
import java.util.Map;

public class OnBlockSpawnType extends SpawnType {
    public static Map<LookupNode, LookupNode> lookupUpNodes = new HashMap<>();

    public OnBlockSpawnType() {
        super();
    }

    @Override
    public void scanBlock(SpawnLocationList list, Searcher searcher) {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    BlockState state = searcher.getBlockState(x, y - 1, z);

                    for (LookupNode node : lookupUpNodes.values()) {
                        if (node.validate(state)) {
                            for (ImmersiveParticleType s : node.types) {
                                list.add(new SpawnLocation(1.0, searcher.cx * 16 + x + 0.5, searcher.cy * 16 + y + 0.5, searcher.cz * 16 + z + 0.5, null, s));
                            }
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
        LookupNode node = new LookupNode();

        // block conditions
        readIdentifierSet(json.get("blocks"), node.blocks, node.blockTags);
        readIdentifierSet(json.get("fluids"), node.fluids, node.fluidTags);

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
