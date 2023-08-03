package immersive_particles.core.spawn_types;

import immersive_particles.core.searcher.Searcher;
import immersive_particles.core.searcher.SpawnLocationList;

public abstract class FullScanSpawnType extends SpawnType {
    abstract void scanBlock(SpawnLocationList list, Searcher searcher, int x, int y, int z);

    @Override
    public void scan(SpawnLocationList list, Searcher searcher) {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    scanBlock(list, searcher, x, y, z);
                }
            }
        }
    }
}
