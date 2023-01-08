package immersive_particles.core;

import net.minecraft.util.math.Direction;

public class SpawnLocation {
    public final double chance;
    public final double x, y, z;
    public final Direction direction;
    public final ImmersiveParticleType type;

    public SpawnLocation(double chance, double x, double y, double z, Direction direction, ImmersiveParticleType type) {
        this.chance = chance;
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = direction;
        this.type = type;
    }
}