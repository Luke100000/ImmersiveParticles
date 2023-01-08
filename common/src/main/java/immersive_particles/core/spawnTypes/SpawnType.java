package immersive_particles.core.spawnTypes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.Searcher;
import immersive_particles.core.SpawnLocationList;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.chunk.ChunkSection;

import java.util.*;


public abstract class SpawnType {
    public SpawnType() {

    }

    // todo move that back to searcher and add caching
    private static BlockState getBlockState(ClientWorld world, ChunkSection chunkSection, int cx, int cy, int cz, int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x > 15 || y > 15 || z > 15) {
            return world.getBlockState(new BlockPos(cx * 16 + x, cy * 16 + y, cz * 16 + z));
        } else {
            return chunkSection.getBlockState(x, y, z);
        }
    }

    public void setWorld() {
        //todo reset any world acceleration data
    }

    public void clear() {

    }

    void readIdentifierSet(JsonElement json, Set<Identifier> identifiers, Set<Identifier> tags) {
        for (JsonElement block : json.getAsJsonArray()) {
            String string = block.getAsString();
            if (string.startsWith("#")) {
                tags.add(new Identifier(string.substring(1)));
            } else {
                identifiers.add(new Identifier(string));
            }
        }
    }

    public abstract void register(JsonObject json, ImmersiveParticleType type);

    public abstract void scanBlock(SpawnLocationList list, Searcher searcher);

    static class LookupNode {
        public Set<Identifier> blocks = new HashSet<>();
        public Set<Identifier> blockTags = new HashSet<>();

        public Set<Identifier> fluids = new HashSet<>();
        public Set<Identifier> fluidTags = new HashSet<>();

        public Direction side;

        public Map<LookupNode, LookupNode> children = new HashMap<>();
        public List<ImmersiveParticleType> types = new LinkedList<>();

        private boolean validateBlock(BlockState state) {
            Identifier id = Registry.BLOCK.getId(state.getBlock());
            for (Identifier i : blocks) {
                if (id.equals(i)) {
                    return true;
                }
            }
            for (Identifier tag : blockTags) {
                if (state.isIn(TagKey.of(Registry.BLOCK_KEY, tag))) {
                    return true;
                }
            }
            return false;
        }

        private boolean validateFluid(FluidState state) {
            Identifier id = Registry.FLUID.getId(state.getFluid());
            for (Identifier i : fluids) {
                if (id.equals(i)) {
                    return true;
                }
            }
            for (Identifier tag : fluidTags) {
                if (state.isIn(TagKey.of(Registry.FLUID_KEY, tag))) {
                    return true;
                }
            }
            return false;
        }

        public boolean validate(BlockState state) {
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
