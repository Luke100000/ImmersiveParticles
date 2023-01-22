package immersive_particles.core;

import immersive_particles.Config;
import immersive_particles.core.spawnTypes.SpawnType;
import immersive_particles.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImmersiveParticlesChunkManager {
    static Executor executor = Executors.newSingleThreadExecutor();

    private static int tick;
    private static int lastWorld;
    private static final ChunkSphere chunkSphere = new ChunkSphere(Config.getInstance().particleSpawnDistanceInChunks);
    public static final Map<Long, SpawnLocationList> chunks = new HashMap<>();
    private static final Set<Long> requested = new HashSet<>();

    private static float updates;

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

        //todo multithread and add catch up, requires particle target life time and last visit timestamp per chunk to determine whether it requires a boost

        updates += chunkSphere.positions.size() * Config.getInstance().chunkUpdatesPerMinute / 60.0f / 20.0f; //todo

        while (updates > 0.0f) {
            Vec3i chunk = chunkSphere.positions.get(tick).add(client.player.getX() / 16.0, client.player.getY() / 16.0, client.player.getZ() / 16.0);
            Optional<SpawnLocationList> locations = getChunk(client.world, chunk.getX(), chunk.getY(), chunk.getZ());

            locations
                    .filter(l -> !l.getAllLocations().isEmpty())
                    .ifPresent(l -> {
                        double v = client.world.random.nextDouble();
                        if (v < l.getTotalChance()) {
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
            }
        }

        return Optional.ofNullable(chunks.get(id));
    }

    private static long toId(int cx, int cy, int cz) {
        return ((long)cx & 0xfffffL) << 40 | ((long)cy & 0xfffffL) << 20 | ((long)cz & 0xfffffL);
    }

    public static List<SpawnLocation> getClose(ImmersiveParticleType type, double px, double py, double pz) {
        return getClose(type, px, py, pz, 8.0, 8);
    }

    public static List<SpawnLocation> getClose(ImmersiveParticleType type, double px, double py, double pz, double distance, int max) {
        List<SpawnLocation> found = new ArrayList<>();
        for (int x = (int)((px - distance) / 16.0); x <= (int)((px + distance) / 16.0); x++) {
            for (int y = (int)((py - distance) / 16.0); y <= (int)((py + distance) / 16.0); y++) {
                for (int z = (int)((pz - distance) / 16.0); z <= (int)((pz + distance) / 16.0); z++) {
                    Optional<SpawnLocationList> chunk = ImmersiveParticlesChunkManager.getChunk(MinecraftClient.getInstance().world, x, y, z);
                    chunk.ifPresent(c -> c.getLocations(type).stream()
                            .filter(p -> Utils.squaredDistance(px, p.x, py, p.y, pz, p.z) < distance * distance)
                            .limit(max)
                            .forEach(found::add));
                }
            }
        }
        return found;
    }
}
