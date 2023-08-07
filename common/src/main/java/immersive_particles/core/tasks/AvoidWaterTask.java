package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import immersive_particles.core.ImmersiveParticleManager;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class AvoidWaterTask extends Task {
    private final AvoidWaterTask.Settings settings;

    public AvoidWaterTask(ImmersiveParticle particle, AvoidWaterTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        // Avoid air
        //todo cache
        BlockState state = ImmersiveParticleManager.getWorld().getBlockState(new BlockPos(particle.x, particle.y, particle.z));
        if (!state.getFluidState().isEmpty()) {
            particle.velocityY = Math.abs(particle.velocityY);
        }
    }

    public static class Settings extends Task.Settings {
        public Settings(JsonObject settings) {

        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new AvoidWaterTask(particle, this);
        }
    }
}

