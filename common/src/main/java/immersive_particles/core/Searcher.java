package immersive_particles.core;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Searcher implements Runnable {
    private final ClientWorld world;
    private final Chunk chunk;
    public final int cx;
    public final int cy;
    public final int cz;
    private final long id;

    Map<Long, BlockState> blockCache = new HashMap<>();
    Map<Long, RegistryEntry<Biome>> biomeCache = new HashMap<>();
    Map<Long, Integer> dayLightCache = new HashMap<>();
    Map<Long, Integer> blockLightCache = new HashMap<>();

    ChunkSection chunkSection;

    public Searcher(ClientWorld world, Chunk chunk, int cx, int cy, int cz, long id) {
        this.world = world;
        this.chunk = chunk;
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.id = id;
    }

    private static long getId(int x, int y, int z) {
        return ((long)x & 0xfffffL) << 40 | ((long)y & 0xfffffL) << 20 | ((long)z & 0xfffffL);
    }

    private BlockPos getPos(int x, int y, int z) {
        return new BlockPos(cx * 16 + x, cy * 16 + y, cz * 16 + z);
    }

    public BlockState getBlockState(int x, int y, int z) {
        long id = getId(x, y, z);
        if (!blockCache.containsKey(id)) {
            if (x < 0 || y < 0 || z < 0 || x > 15 || y > 15 || z > 15) {
                blockCache.put(id, world.getBlockState(getPos(x, y, z)));
            } else {
                blockCache.put(id, chunkSection.getBlockState(x, y, z));
            }
        }
        return blockCache.get(id);
    }

    public RegistryEntry<Biome> getBiome(int x, int y, int z) {
        long id = getId(x, y, z);
        if (!biomeCache.containsKey(id)) {
            if (x < 0 || y < 0 || z < 0 || x > 15 || y > 15 || z > 15) {
                biomeCache.put(id, world.getBiome(getPos(x, y, z)));
            } else {
                biomeCache.put(id, chunkSection.getBiome(x, y, z));
            }
        }
        return biomeCache.get(id);
    }

    public int getDayLight(int x, int y, int z) {
        long id = getId(x, y, z);
        if (!dayLightCache.containsKey(id)) {
            dayLightCache.put(id, world.getLightLevel(LightType.SKY, getPos(x, y, z)));
        }
        return dayLightCache.get(id);
    }

    public int getBlockLight(int x, int y, int z) {
        long id = getId(x, y, z);
        if (!blockLightCache.containsKey(id)) {
            blockLightCache.put(id, world.getLightLevel(LightType.SKY, getPos(x, y, z)));
        }
        return blockLightCache.get(id);
    }

    //todo verify usefulness of cache

    @Override
    public void run() {
        // fetch chunk slice
        int layer = chunk.sectionCoordToIndex(cy);
        chunkSection = chunk.getSectionArray()[layer];

        // collect spawn locations
        SpawnLocationList list = new SpawnLocationList();

        // let the spawn types search for blocks
        SpawnTypes.TYPES.values().forEach(s -> s.scan(list, this));

        // shuffle for extra randomness
        Collections.shuffle(list.getLocations());

        // cache
        ImmersiveParticlesChunkManager.chunks.put(id, list);

        //cleanup
        blockCache.clear();
        biomeCache.clear();
        dayLightCache.clear();
        blockLightCache.clear();
    }
}
