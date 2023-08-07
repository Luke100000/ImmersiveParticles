package immersive_particles.core.searcher;

import immersive_particles.Config;
import immersive_particles.core.ImmersiveParticleManager;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.registries.SpawnTypes;
import immersive_particles.core.spawn_types.SpawnType;
import immersive_particles.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ParticleChunkManager {
    static Executor executor = Executors.newSingleThreadExecutor();

    private static int tick;
    private static int lastWorld;
    private static final ChunkSphere chunkSphere = new ChunkSphere(Config.getInstance().particleSpawnDistanceInChunks);
    public static final Map<Long, SpawnLocationList> chunks = new HashMap<>();
    private static final Map<Long, Integer> newChunks = new HashMap<>();
    private static final Set<Long> requested = new HashSet<>();

    private static float updates;
    private static final AtomicInteger processing = new AtomicInteger();

    public static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return;
        }

        // check if the world has been changed
        if (lastWorld != client.world.hashCode()) {
            lastWorld = client.world.hashCode();
            chunks.clear();
            requested.clear();

            ImmersiveParticleManager.setWorld(client.world);

            // Clear temporary data collected by the spawner
            SpawnTypes.TYPES.values().forEach(SpawnType::setWorld);
        }

        spawn(client);
    }

    private static void spawn(MinecraftClient client) {
        if (client.player == null || client.world == null || client.isPaused()) {
            return;
        }

        updates += chunkSphere.positions.size() * Config.getInstance().chunkUpdatesPerMinute / 60.0f / 20.0f;

        while (updates > 0.0f) {
            Vec3i chunk = chunkSphere.positions.get(tick).add(client.player.getX() / 16.0, client.player.getY() / 16.0, client.player.getZ() / 16.0);
            Optional<SpawnLocationList> locations = getChunk(client.world, chunk.getX(), chunk.getY(), chunk.getZ());

            long id = toId(chunk.getX(), chunk.getY(), chunk.getZ());

            locations
                    .filter(l -> !l.getAllLocations().isEmpty())
                    .ifPresent(l -> {
                        double v = client.world.random.nextDouble();
                        int boost = newChunks.getOrDefault(id, 0);
                        if (v < l.getTotalChance() * (boost > 0 ? 2.0 : 1.0)) {
                            if (v >= l.getTotalChance() && boost > 0) {
                                if (boost > 1) {
                                    newChunks.put(id, boost - 1);
                                } else {
                                    newChunks.remove(id);
                                }
                            }
                            for (Map.Entry<ImmersiveParticleType, List<SpawnLocation>> list : l.getAllLocations().entrySet()) {
                                for (SpawnLocation location : list.getValue()) {
                                    v -= location.chance * Config.getInstance().baseChance / Config.getInstance().chunkUpdatesPerMinute;
                                    if (v < 0) {
                                        v++;
                                        ImmersiveParticleManager.addParticle(list.getKey(), location);
                                    }
                                }
                            }
                        }
                    });

            tick = (tick + 1) % chunkSphere.positions.size();
            updates--;
        }
    }

    public static Optional<SpawnLocationList> getChunk(ClientWorld world, int cx, int cy, int cz) {
        long id = toId(cx, cy, cz);
        if (!requested.contains(id) && !world.isDebugWorld()) {
            Chunk chunk = world.getChunk(cx, cz, ChunkStatus.FULL, false);
            if (chunk != null) {
                executor.execute(new Searcher(world, chunk, cx, cy, cz, id));
                requested.add(id);
                processing.incrementAndGet();
            }
        }

        return Optional.ofNullable(chunks.get(id));
    }

    private static long toId(int cx, int cy, int cz) {
        return ((long) cx & 0xfffffL) << 40 | ((long) cy & 0xfffffL) << 20 | ((long) cz & 0xfffffL);
    }

    public static List<SpawnLocation> getClose(ImmersiveParticleType type, double px, double py, double pz, double distance, int max) {
        List<SpawnLocation> found = new ArrayList<>();
        for (int x = (int) ((px - distance) / 16.0); x <= (int) ((px + distance) / 16.0); x++) {
            for (int y = (int) ((py - distance) / 16.0); y <= (int) ((py + distance) / 16.0); y++) {
                for (int z = (int) ((pz - distance) / 16.0); z <= (int) ((pz + distance) / 16.0); z++) {
                    Optional<SpawnLocationList> chunk = ParticleChunkManager.getChunk(MinecraftClient.getInstance().world, x, y, z);
                    chunk.ifPresent(c -> c.getLocations(type).stream()
                            .filter(p -> Utils.squaredDistance(px, p.x, py, p.y, pz, p.z) < distance * distance)
                            .limit(max)
                            .forEach(found::add));
                }
            }
        }
        return found;
    }

    public static void addChunk(long id, SpawnLocationList list) {
        chunks.put(id, list);
        newChunks.put(id, (int) Math.ceil(Config.getInstance().particleMaxAge / 1200.0 * Config.getInstance().chunkUpdatesPerMinute));
        processing.decrementAndGet();
    }

    public static int getProcessing() {
        return processing.get();
    }
}
