package immersive_particles.core.tasks;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3d;

public class PushTask extends Task {
    private final PushTask.Settings settings;

    private static final Vector3d UP = new Vector3d(0.0, 1.0, 0.0);
    Vector3d lastJump = new Vector3d(UP);
    Vector3d direction;

    private int cooldown;
    private float push;

    public PushTask(ImmersiveParticle particle, PushTask.Settings settings) {
        super(particle);

        this.settings = settings;

        direction = new Vector3d(
                particle.getRandom().nextDouble() - 0.5,
                particle.getRandom().nextDouble() - 0.5,
                particle.getRandom().nextDouble() - 0.5
        ).normalize();
    }

    @Override
    public void tick() {
        push *= settings.relax;

        if (particle.hasCollided()) {
            direction.mul(-1);
        }

        // Rotate towards pushing direction
        particle.rotateTowards(lastJump, settings.inertia);

        // Push
        cooldown--;
        if (cooldown < 0) {
            cooldown = settings.minCooldown + (int) ((settings.maxCooldown - settings.minCooldown) * (particle.getRandom().nextFloat() + 0.75f));

            lastJump = new Vector3d(
                    particle.getRandom().nextDouble() - 0.5,
                    1.0,
                    particle.getRandom().nextDouble() - 0.5
            ).add(direction).normalize().mul(settings.strength);

            particle.velocityX += lastJump.x;
            particle.velocityY += lastJump.y;
            particle.velocityZ += lastJump.z;

            push = 1.0f;
        }

        particle.scaleY = push;
    }

    public static class Settings extends Task.Settings {
        int minCooldown;
        int maxCooldown;
        double strength;
        double relax;
        float inertia;

        public Settings(JsonObject settings) {
            minCooldown = JsonHelper.getInt(settings, "minCooldown", 10);
            maxCooldown = JsonHelper.getInt(settings, "maxCooldown", 20);
            strength = JsonHelper.getFloat(settings, "strength", 0.1f);
            relax = JsonHelper.getFloat(settings, "relax", 0.99f);
            inertia = JsonHelper.getFloat(settings, "inertia", 0.1f);
        }

        @Override
        public Task createTask(ImmersiveParticle particle) {
            return new PushTask(particle, this);
        }
    }
}
