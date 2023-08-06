package immersive_particles.core.renderers;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Vector4d;

public class FishRenderer extends MeshRenderer {
    public FishRenderer(JsonObject particle) {
        super(particle);
    }

    @Override
    void render(ImmersiveParticle particle, VertexConsumer vertexConsumer, float tickDelta, Matrix4d transform, Matrix3f normal, Vector4d position) {
        double strength = particle.getVelocity().length() * 2.0 + 0.1;
        double anim = Math.cos(particle.getAge() + tickDelta) * strength;

        Matrix4d fin = new Matrix4d(transform);

        transform.rotateY(anim);
        renderMeshes(particle, getMeshes("body"), particle.getSprite(), transform, normal, vertexConsumer, tickDelta);

        //todo optimize
        fin.translate(0, 0, 3.0 / 16.0);
        fin.rotateY(anim);
        fin.translate(0, 0, -3.0 / 16.0);
        fin.rotateY(-anim * 2.0);
        renderMeshes(particle, getMeshes("fin"), particle.getSprite(), fin, normal, vertexConsumer, tickDelta);
    }
}
