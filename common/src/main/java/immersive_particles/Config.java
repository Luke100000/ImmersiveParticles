package immersive_particles;

public final class Config extends JsonConfig {
    private static final Config INSTANCE = loadOrCreate();

    public static Config getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unused")
    public String README = "https://github.com/Luke100000/ImmersiveParticles/wiki/Config";

    public final int particleMaxAge = 1200;
    public final int particleSpawnDistanceInChunks = 4;
    public final float chunkUpdatesPerMinute = 2.0f;
    public final float baseChance = 0.2f;
}
