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

public class InBlockSpawnType extends FullScanSpawnType {
    static final List<DescriptorSet> descriptors = new LinkedList<>();

    public InBlockSpawnType() {
        super();
    }

    @Override
    public void scanBlock(SpawnLocationList list, Searcher searcher, int x, int y, int z) {
        for (DescriptorSet descriptor : descriptors) {
            if (descriptor.validate(searcher, x, y, z)) {
                list.add(descriptor.type, new SpawnLocation(descriptor.chanceModifier, searcher.cx * 16 + x + 0.5, searcher.cy * 16 + y + 0.5, searcher.cz * 16 + z + 0.5, BlockPos.ORIGIN));
            }
        }
    }

    @Override
    public void clear() {
        descriptors.clear();
    }

    private static final class DescriptorSet extends Descriptor {
        final BlockDescriptor block;
        final BiomeDescriptor biome;
        final LightDescriptor light;

        final ImmersiveParticleType type;

        //todo weird name as it is the chance
        float chanceModifier;

        DescriptorSet(JsonObject json, ImmersiveParticleType type) {
            this.block = new BlockDescriptor(JsonHelper.getObject(json, "block"));
            this.biome = new BiomeDescriptor(JsonHelper.getObject(json, "biome", new JsonObject()));
            this.light = new LightDescriptor(JsonHelper.getObject(json, "light", new JsonObject()));

            this.type = type;

            chanceModifier = JsonHelper.getFloat(json, "chanceModifier", 1.0f);
        }

        @Override
        protected boolean validate(Searcher searcher, int x, int y, int z) {
            return block.validate(searcher, x, y, z) && biome.validate(searcher, x, y, z) && light.validate(searcher, x, y, z);
        }
    }

    @Override
    public void register(JsonObject json, ImmersiveParticleType type) {
        descriptors.add(new DescriptorSet(json, type));
    }
}
