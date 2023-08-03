package immersive_particles.core.spawn_types.descriptors;

import com.google.gson.JsonObject;
import immersive_particles.core.searcher.Searcher;
import net.minecraft.util.JsonHelper;

public class LightDescriptor extends Descriptor {
    public int minDayLight, maxDayLight;
    public int minBlockLight, maxBlockLight;
    public int minLight, maxLight;
    public boolean hasLightCheck;

    public LightDescriptor(JsonObject json) {
        this.minDayLight = JsonHelper.getInt(json, "minDayLight", 0);
        this.maxDayLight = JsonHelper.getInt(json, "maxDayLight", 15);

        this.minBlockLight = JsonHelper.getInt(json, "minBlockLight", 0);
        this.maxBlockLight = JsonHelper.getInt(json, "maxBlockLight", 15);

        this.minLight = JsonHelper.getInt(json, "minLight", 0);
        this.maxLight = JsonHelper.getInt(json, "maxLight", 15);

        // Skip light check if every level is covered
        if (this.minDayLight > 0 || this.maxDayLight < 15 || this.minBlockLight > 0 || this.maxBlockLight < 15 || this.minLight > 0 || this.maxLight < 15) {
            this.hasLightCheck = true;
        }
    }

    @Override
    public boolean validate(Searcher searcher, int x, int y, int z) {
        if (!hasLightCheck) {
            return true;
        }
        int dayLight = searcher.getDayLight(x, y, z);
        int blockLight = searcher.getBlockLight(x, y, z);
        int light = Math.max(dayLight, blockLight);
        return this.minDayLight <= dayLight && dayLight <= maxDayLight &&
                this.minBlockLight <= blockLight && blockLight <= maxBlockLight &&
                this.minLight <= light && light <= maxLight;
    }
}
