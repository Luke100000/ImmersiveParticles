package immersive_particles.core.particles;

import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.searcher.SpawnLocation;
import immersive_particles.util.obj.FaceVertex;
import org.joml.Vector4d;

public abstract class FlyingParticle extends SimpleParticle {
    public FlyingParticle(ImmersiveParticleType type, SpawnLocation location, ImmersiveParticle leader) {
        super(type, location, leader);
    }

    double getFlap(float tickDelta) {
        return org.joml.Math.cos((age + tickDelta) * 2.0) * 0.5;
    }

    @Override
    Vector4d transformVertex(FaceVertex v, float tickDelta) {
        double flap = getFlap(tickDelta);
        double ox = v.c.r > 0 ? v.c.r * (org.joml.Math.cos(flap) - 1) * v.v.x : 0;
        double oy = v.c.r > 0 ? v.c.r * org.joml.Math.sin(flap) * Math.abs(v.v.x) : 0;
        return new Vector4d((v.v.x + ox) / 16.0f, (v.v.y + oy) / 16.0f, v.v.z / 16.0f, 1.0f);
    }
}
