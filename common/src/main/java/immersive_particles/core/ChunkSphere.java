package immersive_particles.core;

import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChunkSphere {
    public final int size;
    public final List<Vec3i> positions = new ArrayList<>();

    public ChunkSphere(int size) {
        this.size = size;

        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                for (int z = -size; z <= size; z++) {
                    if (x * x + y * y + z * z < size * size) {
                        positions.add(new Vec3i(x, y, z));
                    }
                }
            }
        }

        Collections.shuffle(positions);
    }
}
