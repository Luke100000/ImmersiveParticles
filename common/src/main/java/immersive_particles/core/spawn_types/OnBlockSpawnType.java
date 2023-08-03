package immersive_particles.core.spawn_types;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.searcher.Searcher;
import immersive_particles.core.searcher.SpawnLocation;
import immersive_particles.core.searcher.SpawnLocationList;
import immersive_particles.core.spawn_types.descriptors.BiomeDescriptor;
import immersive_particles.core.spawn_types.descriptors.BlockDescriptor;
import immersive_particles.core.spawn_types.descriptors.Descriptor;
import immersive_particles.core.spawn_types.descriptors.LightDescriptor;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedList;
import java.util.List;

public class OnBlockSpawnType extends FullScanSpawnType {
    static final List<DescriptorSet> descriptors = new LinkedList<>();

    public OnBlockSpawnType() {
        super();
    }

    @Override
    public void scanBlock(SpawnLocationList list, Searcher searcher, int x, int y, int z) {
        for (DescriptorSet descriptor : descriptors) {
            if (descriptor.validate(searcher, x, y, z)) {
                for (BlockPos offset : descriptor.side.getOffsets()) {
                    int px = x + offset.getX();
                    int py = y + offset.getY();
                    int pz = z + offset.getZ();
                    if (descriptor.validateSecond(searcher, px, py, pz)) {
                        list.add(descriptor.type, new SpawnLocation(descriptor.chanceModifier, searcher.cx * 16 + x + 0.5, searcher.cy * 16 + y + 0.5, searcher.cz * 16 + z + 0.5, offset));
                    }
                }
            }
        }
    }

    @Override
    public void clear() {
        descriptors.clear();
    }

    private static final class DescriptorSet extends Descriptor {
        final BlockDescriptor block;
        final BlockDescriptor onBlock;
        final BiomeDescriptor biome;
        final LightDescriptor light;

        public Side side;

        final ImmersiveParticleType type;

        float chanceModifier;

        DescriptorSet(JsonObject json, ImmersiveParticleType type) {
            this.block = new BlockDescriptor(JsonHelper.getObject(json, "inBlock"));
            this.onBlock = new BlockDescriptor(JsonHelper.getObject(json, "onBlock"));
            this.biome = new BiomeDescriptor(JsonHelper.getObject(json, "biome", new JsonObject()));
            this.light = new LightDescriptor(JsonHelper.getObject(json, "light", new JsonObject()));

            // The side the emitter is attached to
            String string = JsonHelper.getString(json, "side", "all");
            switch (string) {
                case "sides" -> this.side = Side.SIDES;
                case "top" -> this.side = Side.TOP;
                case "bottom" -> this.side = Side.BOTTOM;
                default -> this.side = Side.ALL;
            }

            this.type = type;

            chanceModifier = JsonHelper.getFloat(json, "chanceModifier", 1.0f);
        }

        @Override
        protected boolean validate(Searcher searcher, int x, int y, int z) {
            return block.validate(searcher, x, y, z) && biome.validate(searcher, x, y, z) && light.validate(searcher, x, y, z);
        }

        public boolean validateSecond(Searcher searcher, int x, int y, int z) {
            return onBlock.validate(searcher, x, y, z);
        }
    }

    @Override
    public void register(JsonObject json, ImmersiveParticleType type) {
        descriptors.add(new DescriptorSet(json, type));
    }
}
