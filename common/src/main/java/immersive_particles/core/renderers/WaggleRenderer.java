package immersive_particles.core.renderers;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import immersive_particles.util.Meth;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.JsonHelper;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Vector4d;

public class WaggleRenderer extends MeshRenderer {
    private final float offset;
    private final float baseStrength;
    private final float speedStrength;

    public WaggleRenderer(JsonObject particle) {
        super(particle);

        offset = JsonHelper.getFloat(particle, "offset", 3.5f);
        baseStrength = JsonHelper.getFloat(particle, "baseStrength", 0.1f);
        speedStrength = JsonHelper.getFloat(particle, "speedStrength", 2.0f);
    }

    @Override
    void render(ImmersiveParticle particle, VertexConsumer vertexConsumer, float tickDelta, Matrix4d transform, Matrix3f normal, Vector4d position) {
        double strength = particle.getVelocity().length() * speedStrength + baseStrength;
        double anim = Meth.sin(particle.getAge() + tickDelta) * strength;

        Matrix4d fin = new Matrix4d(transform);

        transform.rotateY(anim);
        renderMeshes(particle, getMeshes("body"), particle.getSprite(), transform, normal, vertexConsumer, tickDelta);

        //todo optimize
        fin.translate(0, 0, offset / 16.0);
        fin.rotateY(anim);
        fin.translate(0, 0, -offset / 16.0);
        fin.rotateY(-anim * 2.0);
        renderMeshes(particle, getMeshes("fin"), particle.getSprite(), fin, normal, vertexConsumer, tickDelta);
    }
}
