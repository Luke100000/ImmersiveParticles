package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import immersive_particles.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

public class AvoidPlayerTask extends Task {
    private final Settings settings;

    public AvoidPlayerTask(ImmersiveParticle particle, Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            Vec3d pos = player.getPos();
            double v = Math.sqrt(Utils.squaredDistance(pos.x, particle.getX(), pos.y, particle.getY(), pos.z, particle.getZ()));
            if (v < settings.avoidPlayerDistance * (1.0 + player.getVelocity().length() * settings.playerSpeedPanic)) {
                double speed = settings.strength / (v + 0.01);
                particle.velocityX += (particle.x - pos.x) * speed;
                particle.velocityY += 0.02 * speed;
                particle.velocityZ += (particle.y - pos.y) * speed;
            }
        }
    }

    public static class Settings extends Task.Settings {
        double avoidPlayerDistance;
        double playerSpeedPanic;
        double strength;

        public Settings(JsonObject settings) {
            avoidPlayerDistance = JsonHelper.getDouble(settings, "avoidPlayerDistance", 1.0);
            playerSpeedPanic = JsonHelper.getDouble(settings, "playerSpeedPanic", 1.0);
            strength = JsonHelper.getDouble(settings, "strength", 0.1);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new AvoidPlayerTask(particle, this);
        }
    }
}
