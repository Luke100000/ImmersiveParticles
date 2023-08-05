package immersive_particles.core.renderers;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import immersive_particles.util.obj.FaceVertex;
import org.joml.Vector4d;

public class FlappingRenderer extends MeshRenderer {
    public FlappingRenderer(JsonObject particle) {
        super(particle);
    }

    double getFlap(ImmersiveParticle particle, float tickDelta) {
        return org.joml.Math.cos((particle.getAge() + tickDelta) * 2.0) * 0.5;
    }

    @Override
    Vector4d transformVertex(ImmersiveParticle particle, FaceVertex v, float tickDelta) {
        double flap = getFlap(particle, tickDelta);
        double ox = v.c.r > 0 ? v.c.r * (org.joml.Math.cos(flap) - 1) * v.v.x : 0;
        double oy = v.c.r > 0 ? v.c.r * org.joml.Math.sin(flap) * Math.abs(v.v.x) : 0;
        return new Vector4d((v.v.x + ox) / 16.0f, (v.v.y + oy) / 16.0f, v.v.z / 16.0f, 1.0f);
    }
}
