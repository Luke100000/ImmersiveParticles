package immersive_particles.core.spawnTypes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.Searcher;
import immersive_particles.core.SpawnLocationList;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.*;


public abstract class SpawnType {
    public SpawnType() {

    }

    public void setWorld() {
        //todo reset any world acceleration data
    }

    public void clear() {

    }

    static void readIdentifierSet(JsonElement json, Set<Identifier> identifiers, Set<Identifier> tags) {
        if (json != null) {
            for (JsonElement block : json.getAsJsonArray()) {
                String string = block.getAsString();
                if (string.startsWith("#")) {
                    tags.add(new Identifier(string.substring(1)));
                } else {
                    identifiers.add(new Identifier(string));
                }
            }
        }
    }

    public abstract void register(JsonObject json, ImmersiveParticleType type);

    public abstract void scan(SpawnLocationList list, Searcher searcher);

    static class LookupNode {
        public Set<Identifier> blocks = new HashSet<>();
        public Set<Identifier> blockTags = new HashSet<>();

        public Set<Identifier> fluids = new HashSet<>();
        public Set<Identifier> fluidTags = new HashSet<>();

        public Direction side;

        public Map<LookupNode, LookupNode> children = new HashMap<>();
        public List<ImmersiveParticleType> types = new LinkedList<>();

        public static LookupNode fromJson(JsonObject json) {
            LookupNode node = new LookupNode();

            // block conditions
            readIdentifierSet(json.get("blocks"), node.blocks, node.blockTags);
            readIdentifierSet(json.get("fluids"), node.fluids, node.fluidTags);

            return node;
        }

        private boolean validateBlock(BlockState state) {
            Identifier id = Registry.BLOCK.getId(state.getBlock());
            boolean empty = true;
            for (Identifier i : blocks) {
                empty = false;
                if (id.equals(i)) {
                    return true;
                }
            }
            for (Identifier tag : blockTags) {
                empty = false;
                if (state.isIn(TagKey.of(Registry.BLOCK_KEY, tag))) {
                    return true;
                }
            }
            return empty;
        }

        private boolean validateFluid(FluidState state) {
            Identifier id = Registry.FLUID.getId(state.getFluid());
            boolean empty = true;
            for (Identifier i : fluids) {
                empty = false;
                if (id.equals(i)) {
                    return true;
                }
            }
            for (Identifier tag : fluidTags) {
                empty = false;
                if (state.isIn(TagKey.of(Registry.FLUID_KEY, tag))) {
                    return true;
                }
            }
            return empty;
        }

        public boolean validate(Searcher searcher, int x, int y, int z) {
            BlockState state = searcher.getBlockState(x, y, z);
            //todo verify biome, temperature and light
            return validateBlock(state) && validateFluid(state.getFluidState());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LookupNode node)) return false;
            return Objects.equals(blocks, node.blocks) && Objects.equals(blockTags, node.blockTags) && Objects.equals(fluids, node.fluids) && Objects.equals(fluidTags, node.fluidTags) && side == node.side;
        }

        @Override
        public int hashCode() {
            return Objects.hash(blocks, blockTags, fluids, fluidTags, side);
        }
    }
}
