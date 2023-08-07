package immersive_particles.core.searcher;

import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;

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

    public Vector3d getRandomPosition(float w, float h, float d) {
        if (true) {
            return new Vector3d(x, y, z);
        }
        Vector3d rand = new Vector3d(
                offset.getX() == 0.0 ? (random.nextDouble() - 0.5) : -offset.getX() / 2.0,
                offset.getY() == 0.0 ? (random.nextDouble() - 0.5) : -offset.getY() / 2.0,
                offset.getZ() == 0.0 ? (random.nextDouble() - 0.5) : -offset.getZ() / 2.0
        );
        return new Vector3d(x, y, z).add(rand.mul(1 - w, 1 - h, 1 - d));
    }
}