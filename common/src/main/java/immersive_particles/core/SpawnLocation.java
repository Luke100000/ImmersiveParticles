package immersive_particles.core;

import net.minecraft.util.math.BlockPos;

public class SpawnLocation {
    public final double chance;
    public final double x, y, z;
    public final BlockPos offset;

    public SpawnLocation(double chance, double x, double y, double z, BlockPos offset) {
        this.chance = chance;
        this.x = x;
        this.y = y;
        this.z = z;
        this.offset = offset;
    }
}