package immersive_particles.core.particles;

import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.searcher.SpawnLocation;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.JsonHelper;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

public class SwimmingSwarmParticle extends SimpleParticle {
    private final Sprite sprite;

    private final float swarmSize;

    private final Vector3d target;

    private int nextTarget;

    public SwimmingSwarmParticle(ImmersiveParticleType type, SpawnLocation location, ImmersiveParticle leader) {
        super(type, location, leader);

        this.sprite = type.getSprites().get(random.nextInt(type.getSprites().size()));

        swarmSize = JsonHelper.getFloat(type.behavior, "swarmSize", 1.0f);

        target = new Vector3d(x, y, z);
    }

    @Override
    void render(VertexConsumer vertexConsumer, float tickDelta, Matrix4d transform, Matrix3f normal, Vector4d position) {
        double strength = getVelocity().length() * 2.0 + 0.1;
        double anim = Math.cos(age + tickDelta) * strength;

        Matrix4d fin = new Matrix4d(transform);

        transform.rotateY(anim);
        renderObject(getMeshes("body"), sprite, transform, normal, vertexConsumer, tickDelta);

        //todo optimize
        fin.translate(0 , 0, 3.0 / 16.0);
        fin.rotateY(anim);
        fin.translate(0 , 0, -3.0 / 16.0);
        fin.rotateY(-anim * 2.0);
        renderObject(getMeshes("fin"), sprite, fin, normal, vertexConsumer, tickDelta);
    }

    @Override
    public boolean tick() {
        if (shouldUpdate()) {
            if (leader != null && nextTarget < 20) {
                double dist = getSquaredDistanceTo(leader);
                float range = swarmSize * swarmSize;
                if (dist > range) {
                    target.add(
                            (random.nextDouble() - 0.5) * range, //todo behavior
                            (random.nextDouble() - 0.5) * range,
                            (random.nextDouble() - 0.5) * range
                    );
                    nextTarget = 40;
                }
            }

            // Next target
            nextTarget--;
            if (nextTarget < 0) {
                float range = leader == null ? 20.0f : 5.0f;
                int coolDown = leader == null ? 50 : 20;
                nextTarget = random.nextInt(coolDown) + coolDown; //todo behavior
                target.add(
                        (random.nextDouble() - 0.5) * range, //todo behavior
                        (random.nextDouble() - 0.5) * range,
                        (random.nextDouble() - 0.5) * range
                );
            }

            // Move to the target
            moveTo(target, 0.015f);

            // Look to target
            //todo not fully correct when panicking, instead of pushing, a new target outside the player range should be used
            //for insects its generally fine but fish don't hover, they have to face the target
            rotateToTarget(target, 0.2f);

            // Panic
            avoidPlayer();

            // Avoid air
            avoidAir();
        }

        return super.tick();
    }
}