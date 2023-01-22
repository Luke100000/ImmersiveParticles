package immersive_particles.core.particles;

import immersive_particles.Main;
import immersive_particles.core.ImmersiveParticleManager;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.SpawnLocation;
import immersive_particles.util.obj.Mesh;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Vector3d;

public class SwimmingSwarmParticle extends ImmersiveParticle {
    private final Sprite sprite;

    private final Mesh bodyMesh;
    private final Mesh finMesh;

    private final float swarmSize;

    private final Vector3d target;

    private int nextTarget;

    public SwimmingSwarmParticle(ImmersiveParticleType type, SpawnLocation location) {
        super(type, location);

        this.sprite = type.getSprites().get(random.nextInt(type.getSprites().size()));

        String object = JsonHelper.getString(type.behavior, "object");
        bodyMesh = getMesh(Main.locate(object), JsonHelper.getString(type.behavior, "bodyMesh"));
        finMesh = getMesh(Main.locate(object), JsonHelper.getString(type.behavior, "finMesh"));

        swarmSize = JsonHelper.getFloat(type.behavior, "swarmSize", 1.0f);

        target = new Vector3d(x, y, z);
    }

    @Override
    void render(VertexConsumer vertexConsumer, float tickDelta, Matrix4d transform, Matrix3f normal) {
        renderObject(bodyMesh, sprite, transform, normal, vertexConsumer, tickDelta);
        renderObject(finMesh, sprite, transform, normal, vertexConsumer, tickDelta);
    }

    @Override
    public boolean tick() {
        if (visible) {
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
            rotateToTarget(target, 0.2f);

            // Panic
            avoidPlayer();

            // Avoid air
            //todo cache
            BlockState state = ImmersiveParticleManager.getWorld().getBlockState(new BlockPos(x, y, z));
            if (state.getFluidState().isEmpty()) {
                velocityY = -Math.abs(velocityY);
            }
        }

        return super.tick();
    }
}