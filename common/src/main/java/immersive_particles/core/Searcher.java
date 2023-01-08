package immersive_particles.core;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import java.util.Collections;

public record Searcher(ClientWorld world, Chunk chunk, int cx, int cy, int cz, long id) implements Runnable {
    @Override
    public void run() {
        // fetch chunk slice
        int layer = chunk.sectionCoordToIndex(cy);
        ChunkSection chunkSection = chunk.getSectionArray()[layer];

        // collect spawn locations
        SpawnLocationList list = new SpawnLocationList();

        // let the spawn types search for blocks
        SpawnTypes.TYPES.values().forEach(s -> s.scanBlock(list, world, chunkSection, cx, cy, cz));

        // shuffle for extra randomness
        Collections.shuffle(list.getLocations());

        // cache
        ParticleChunkManager.chunks.put(id, list);
    }
}
