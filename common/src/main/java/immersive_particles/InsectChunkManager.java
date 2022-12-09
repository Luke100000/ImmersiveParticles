package immersive_particles;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InsectChunkManager {
    static Executor executor = Executors.newSingleThreadExecutor();

    private static int tick;
    private static int lastWorld;
    private static final ChunkSphere chunkSphere = new ChunkSphere(Config.getInstance().particleSpawnDistanceInChunks);
    private static final Map<Long, SpawnLocationList> chunks = new HashMap<>();
    private static final Set<Long> requested = new HashSet<>();

    private static float updates;

    public static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return;
        }

        if (lastWorld != client.world.hashCode()) {
            lastWorld = client.world.hashCode();
            chunks.clear();
        }

        spawn(client);
    }

    private static void spawn(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return;
        }

        updates += chunkSphere.positions.size() * Config.getInstance().chunkUpdatedPerMinute / 60.0f;

        while (updates > 0.0f) {
            Vec3i chunk = chunkSphere.positions.get(tick).add(client.player.getX() / 16.0, client.player.getY() / 16.0, client.player.getZ() / 16.0);
            Optional<SpawnLocationList> locations = fetchChunk(client.world, chunk.getX(), chunk.getY(), chunk.getZ());

            locations
                    .filter(l -> !l.getLocations().isEmpty())
                    .ifPresent(l -> {
                        double v = client.world.random.nextDouble();

                        if (v < l.getTotalChance()) {
                            for (SpawnLocation location : l.getLocations()) {
                                v -= location.chance;

                                if (v < 0) {
                                    v++;

                                    client.world.addParticle(Particles.FLY,
                                            location.x,
                                            location.y,
                                            location.z,
                                            0,
                                            0,
                                            0);
                                }
                            }
                        }
                    });

            tick = (tick + 1) % chunkSphere.positions.size();
            updates--;
        }
    }

    private static BlockState getBlockState(ClientWorld world, ChunkSection chunkSection, int cx, int cy, int cz, int x, int y, int z) {
        if (x <0 || y < 0 || z < 0 || x > 15 || y > 15 || z > 15) {
            return world.getBlockState(new BlockPos(cx * 16 + x, cy * 16 + y, cz * 16 + z));
        } else {
            return chunkSection.getBlockState(x, y, z);
        }
    }

    private static Optional<SpawnLocationList> fetchChunk(ClientWorld world, int cx, int cy, int cz) {
        long id = toId(cx, cy, cz);
        if (!requested.contains(id)) {
            requested.add(id);
            Chunk chunk = world.getChunk(cx, cz, ChunkStatus.FULL, false);
            if (chunk != null && !world.isDebugWorld()) {
                executor.execute(
                        () -> {
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
                            chunks.put(id, list);
                        }
                );
            }
        }

        return Optional.ofNullable(chunks.get(id));
    }

    private static long toId(int cx, int cy, int cz) {
        return ((long)cx & 0xffffffffL) << 40 | ((long)cy & 0xffffffffL) << 20 | ((long)cz & 0xffffffffL);
    }
}
