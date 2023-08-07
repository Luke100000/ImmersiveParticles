package immersive_particles.core.renderers;

import immersive_particles.core.ImmersiveParticle;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Vector4d;

public abstract class ParticleRenderer {
    public void render(ImmersiveParticle particle, VertexConsumer vertexConsumer, Vec3d camera, float tickDelta) {
        double x = particle.getX(tickDelta) - camera.getX();
        double y = particle.getY(tickDelta) - camera.getY();
        double z = particle.getZ(tickDelta) - camera.getZ();

        Vector4d position = new Vector4d(x, y, z, 1.0);

        double yaw = particle.getYaw(tickDelta);
        double pitch = particle.getPitch(tickDelta);
        double roll = particle.getRoll(tickDelta);

        Matrix4d transform = new Matrix4d();
        transform.rotationYXZ(yaw, pitch, roll);
        float a = Math.min(particle.getMaxAge(), particle.getAge() + tickDelta);
        float transition = 0.1f;
        transform.scale(Math.min(1.0f, Math.min(a * transition, (particle.getMaxAge() - a) * transition)));
        transform.setColumn(3, position);

        Matrix3f normal = new Matrix3f();
        normal.rotationXYZ((float) pitch, (float) yaw, (float) roll);

        render(particle, vertexConsumer, tickDelta, transform, normal, position);
    }

    abstract void render(ImmersiveParticle particle, VertexConsumer vertexConsumer, float tickDelta, Matrix4d transform, Matrix3f normal, Vector4d position);
}
