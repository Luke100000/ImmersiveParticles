package immersive_particles.core.particles;

import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.searcher.SpawnLocation;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

public class JellyfishParticle extends SimpleParticle {
    private static final Vector3d UP = new Vector3d(0.0, 1.0, 0.0);
    Vector3d lastJump = new Vector3d(UP);
    Vector3d direction;

    float push = 0.0f;

    public JellyfishParticle(ImmersiveParticleType type, SpawnLocation location, ImmersiveParticle leader) {
        super(type, location, leader);

        velocityMultiplier = 0.9f;

        direction = new Vector3d(
                random.nextDouble() - 0.5,
                random.nextDouble() - 0.5,
                random.nextDouble() - 0.5
        ).normalize();
    }

    @Override
    void render(VertexConsumer vertexConsumer, float tickDelta, Matrix4d transform, Matrix3f normal, Vector4d position) {
        transform.scale(1.0f, 1.0f, 1.0f + Math.sin((1.0f - push) * 5.0f) * push + Math.cos(age + tickDelta) * 0.1f);
        super.render(vertexConsumer, tickDelta, transform, normal, position);
    }

    @Override
    public boolean tick() {
        if (shouldUpdate()) {
            // Push
            if (age % 10 == 0 && random.nextInt(10) == 0) {
                lastJump = new Vector3d(
                        random.nextDouble() - 0.5,
                        1.0,
                        random.nextDouble() - 0.5
                ).add(direction).normalize().mul(0.1);

                velocityX += lastJump.x;
                velocityY += lastJump.y;
                velocityZ += lastJump.z;

                push = 1.0f;
            }

            // Lean towards last jump position
            rotateTowards(lastJump, 0.1f);

            // Relax
            push *= 0.9f;

            // Panic
            //similar to cod, let getPlayerAvoidingPos() return a optional position to where to flee, use it to push if push is larger than 0.5
            avoidPlayer();

            // Avoid air
            if (avoidAir()) {
                direction.y = -Math.abs(direction.y);
            }
        }

        return super.tick();
    }
}
