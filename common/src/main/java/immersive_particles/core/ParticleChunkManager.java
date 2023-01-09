package immersive_particles.core;

import immersive_particles.Config;
import immersive_particles.Main;
import immersive_particles.core.spawnTypes.SpawnType;
import immersive_particles.resources.ParticleManagerLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ParticleChunkManager {
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

        updates += chunkSphere.positions.size() * Config.getInstance().chunkUpdatedPerMinute / 60.0f / 20.0f;

        while (updates > 0.0f) {
            Vec3i chunk = chunkSphere.positions.get(tick).add(client.player.getX() / 16.0, client.player.getY() / 16.0, client.player.getZ() / 16.0);
            Optional<SpawnLocationList> locations = fetchChunk(client.world, chunk.getX(), chunk.getY(), chunk.getZ());

            locations
                    .filter(l -> !l.getLocations().isEmpty())
                    .ifPresent(l -> {
                        double v = client.world.random.nextDouble();
                        if (v < l.getTotalChance()) {
                            for (SpawnLocation location : l.getLocations()) {
                                v -= location.chance * Config.getInstance().baseChance / Config.getInstance().chunkUpdatedPerMinute;
                                if (v < 0) {
                                    v++;
                                    //todo
                                    ImmersiveParticleManager.addParticle(ParticleManagerLoader.PARTICLES.get(Main.locate("bumblebee")), location);
                                }
                            }
                        }
                    });

            tick = (tick + 1) % chunkSphere.positions.size();
            updates--;
        }
    }

    private static Optional<SpawnLocationList> fetchChunk(ClientWorld world, int cx, int cy, int cz) {
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
}
