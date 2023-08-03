package immersive_particles.core.spawn_types.descriptors;

import com.google.gson.JsonObject;
import immersive_particles.core.searcher.Searcher;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.HashSet;
import java.util.Set;

public class BiomeDescriptor extends Descriptor {
    private static final Identifier EMPTY = new Identifier("empty:empty");

    public Set<Identifier> biomes = new HashSet<>();
    public Set<Identifier> biomeTags = new HashSet<>();

    public float minTemperature, maxTemperature;
    public float minDownfall, maxDownfall;

    public BiomeDescriptor(JsonObject json) {
        readIdentifierSet(json.get("biomes"), this.biomes, this.biomeTags);

        this.minTemperature = JsonHelper.getFloat(json, "minTemperature", -100.0f);
        this.maxTemperature = JsonHelper.getFloat(json, "maxTemperature", 100.0f);

        this.minDownfall = JsonHelper.getFloat(json, "minDownfall", 0.0f);
        this.maxDownfall = JsonHelper.getFloat(json, "maxDownfall", 1.0f);
    }

    @Override
    public boolean validate(Searcher searcher, int x, int y, int z) {
        RegistryEntry<Biome> biome = searcher.getBiome(x, y, z);

        // Climate check
        float temperature = biome.value().getTemperature();
        float downfall = biome.value().getDownfall();
        if (temperature < minTemperature || temperature > maxTemperature || downfall < minDownfall || downfall > maxDownfall) {
            return false;
        }

        return validateIdentifiers(biomes, biomeTags, () -> biome.getKey().map(RegistryKey::getValue).orElse(EMPTY), tag -> biome.isIn(TagKey.of(Registry.BIOME_KEY, tag)));
    }
}
