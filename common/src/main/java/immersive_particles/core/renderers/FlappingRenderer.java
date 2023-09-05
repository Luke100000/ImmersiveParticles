package immersive_particles.core.renderers;

import com.google.gson.JsonObject;
import immersive_particles.core.ImmersiveParticle;
import immersive_particles.util.Meth;
import immersive_particles.util.obj.FaceVertex;
import net.minecraft.util.JsonHelper;
import org.joml.Vector4d;

public class FlappingRenderer extends MeshRenderer {
    private final float strength;
    private final float speed;

    public FlappingRenderer(JsonObject particle) {
        super(particle);

        strength = JsonHelper.getFloat(particle, "strength", 0.5f);
        speed = JsonHelper.getFloat(particle, "speed", 2.0f);
    }

    float getFlappingSpeed(ImmersiveParticle particle) {
        return switch (particle.getState()) {
            case IDLE -> 0.75f;
            case DEAD -> 0.0f;
            default -> 1.0f;
        };
    }

    float getFlap(ImmersiveParticle particle, float tickDelta) {
        return Meth.sin((particle.getAge() + tickDelta) * getFlappingSpeed(particle) * speed) * strength;
    }

    @Override
    Vector4d transformVertex(ImmersiveParticle particle, FaceVertex v, float tickDelta) {
        float flap = getFlap(particle, tickDelta);
        float ox = v.c.r > 0 ? v.c.r * (Meth.cos(flap) - 1) * v.v.x : 0;
        float oy = v.c.r > 0 ? v.c.r * Meth.sin(flap) * Math.abs(v.v.x) : 0;
        return new Vector4d((v.v.x + ox) / 16.0f, (v.v.y + oy) / 16.0f, v.v.z / 16.0f, 1.0f);
    }
}
