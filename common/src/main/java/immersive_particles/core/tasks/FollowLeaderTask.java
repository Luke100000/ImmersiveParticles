package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import immersive_particles.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

public class FollowLeaderTask extends Task{
    private final FollowLeaderTask.Settings settings;

    public FollowLeaderTask(ImmersiveParticle particle, FollowLeaderTask.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick(ImmersiveParticle particle) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            Vec3d pos = player.getPos();
            double v = Math.sqrt(Utils.squaredDistance(pos.x, particle.getX(), pos.y, particle.getY(), pos.z, particle.getZ()));
            if (v < settings.avoidPlayerDistance * (1.0 + player.getVelocity().length() * 20.0)) {
                double speed = 0.1 / (v + 0.01);
                particle.velocityX += (particle.x - pos.x) * speed;
                particle.velocityY += (particle.y - pos.y) * speed;
                particle.velocityZ += 0.02 * speed;
            }
        }
    }

    public static class Settings extends Task.Settings {
        double avoidPlayerDistance;

        public Settings(JsonObject settings) {
            avoidPlayerDistance = JsonHelper.getDouble(settings, "avoidPlayerDistance", 1.0);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new FollowLeaderTask(particle, this);
        }
    }
}
