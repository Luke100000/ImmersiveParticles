package immersive_particles.core.spawnTypes.descriptors;

import com.google.gson.JsonObject;
import immersive_particles.core.searcher.Searcher;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Set;

public class BlockDescriptor extends Descriptor {
    public Set<Identifier> blocks = new HashSet<>();
    public Set<Identifier> blockTags = new HashSet<>();

    public Set<Identifier> fluids = new HashSet<>();
    public Set<Identifier> fluidTags = new HashSet<>();

    public BlockDescriptor(JsonObject json) {
        readIdentifierSet(json.get("blocks"), this.blocks, this.blockTags);
        readIdentifierSet(json.get("fluids"), this.fluids, this.fluidTags);
    }

    private boolean validateBlock(BlockState state) {
        return validateIdentifiers(blocks, blockTags, () -> Registry.BLOCK.getId(state.getBlock()), (tag) -> state.isIn(TagKey.of(Registry.BLOCK_KEY, tag)));
    }

    private boolean validateFluid(FluidState state) {
        return validateIdentifiers(fluids, fluidTags, () -> Registry.FLUID.getId(state.getFluid()), (tag) -> state.isIn(TagKey.of(Registry.FLUID_KEY, tag)));
    }

    @Override
    public boolean validate(Searcher searcher, int x, int y, int z) {
        BlockState state = searcher.getBlockState(x, y, z);
        return validateBlock(state) && validateFluid(state.getFluidState());
    }
}
