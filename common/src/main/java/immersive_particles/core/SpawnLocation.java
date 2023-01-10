package immersive_particles.core;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class SpawnLocation {
    public final double chance;
    public final double x, y, z;
    public final BlockPos offset;
    private static final Random random = new Random();

    public SpawnLocation(double chance, double x, double y, double z, BlockPos offset) {
        this.chance = chance;
        this.x = x;
        this.y = y;
        this.z = z;
        this.offset = offset;
    }

    public Vec3d getRandomPosition(float w, float h, float d) {
        //todo depends on the offset
        return new Vec3d(x + (random.nextDouble() - 0.5) * (1.0 - w), y + (random.nextDouble() - 0.5) * (1.0 - h), z + (random.nextDouble() - 0.5) * (1.0 - d));
    }
}