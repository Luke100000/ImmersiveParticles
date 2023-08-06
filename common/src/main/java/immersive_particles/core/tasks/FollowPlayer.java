package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

public class FollowPlayer extends Task {
    private final FollowPlayer.Settings settings;

    public FollowPlayer(ImmersiveParticle particle, FollowPlayer.Settings settings) {
        super(particle);
        this.settings = settings;
    }

    @Override
    public void tick() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (MinecraftClient.getInstance().player != null) {
            Vector3d position = new Vector3d(player.getX(), player.getY(), player.getZ());
            double distance = particle.getSquaredDistanceTo(position);
            if (distance > settings.maxDistance * settings.maxDistance) {
                Vector3d target = position.sub(particle.getPosition()).normalize().mul(settings.minDistance).add(particle.getPosition());
                particle.setTarget(target);
            }
        }
    }

    public static class Settings extends Task.Settings {
        double minDistance;
        double maxDistance;

        public Settings(JsonObject settings) {
            minDistance = JsonHelper.getDouble(settings, "minDistance", 1);
            maxDistance = JsonHelper.getDouble(settings, "maxDistance", 3);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new FollowPlayer(particle, this);
        }
    }
}
