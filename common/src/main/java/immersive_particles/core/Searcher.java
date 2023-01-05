package immersive_particles.core;

import immersive_particles.InsectChunkManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import java.util.Collections;

public record Searcher(ClientWorld world, Chunk chunk, int cx, int cy, int cz, long id) implements Runnable {
    private static BlockState getBlockState(ClientWorld world, ChunkSection chunkSection, int cx, int cy, int cz, int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x > 15 || y > 15 || z > 15) {
            return world.getBlockState(new BlockPos(cx * 16 + x, cy * 16 + y, cz * 16 + z));
        } else {
            return chunkSection.getBlockState(x, y, z);
        }
    }

    @Override
    public void run() {
        // fetch chunk slice
        int layer = chunk.sectionCoordToIndex(cy);
        ChunkSection chunkSection = chunk.getSectionArray()[layer];

        // collect spawn locations
        SpawnLocationList list = new SpawnLocationList();
        if (!chunkSection.isEmpty()) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        if (getBlockState(world, chunkSection, cx, cy, cz, x, y - 1, z).getBlock() == Blocks.GRASS_BLOCK && chunkSection.getBlockState(x, y, z).getBlock() == Blocks.AIR) {
                            list.add(new SpawnLocation(0.01, cx * 16 + x + 0.5, cy * 16 + y + 0.5, cz * 16 + z + 0.5, 0.0, 0.0, 0.0));
                        }
                    }
                }
            }
        }

        //shuffle for extra randomness
        Collections.shuffle(list.getLocations());

        // cache
        InsectChunkManager.chunks.put(id, list);
    }
}
