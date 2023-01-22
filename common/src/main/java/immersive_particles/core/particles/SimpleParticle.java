package immersive_particles.core.particles;

import immersive_particles.Main;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.SpawnLocation;
import immersive_particles.util.obj.Mesh;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.JsonHelper;
import org.joml.Matrix3f;
import org.joml.Matrix4d;

public class SimpleParticle extends ImmersiveParticle {
    private final Sprite sprite;
    private final Mesh mesh;

    public SimpleParticle(ImmersiveParticleType type, SpawnLocation location) {
        super(type, location);

        this.sprite = type.getSprites().get(random.nextInt(type.getSprites().size()));

        mesh = getMesh(Main.locate(JsonHelper.getString(type.behavior, "object")), JsonHelper.getString(type.behavior, "mesh"));
    }

    @Override
    void render(VertexConsumer vertexConsumer, float tickDelta, Matrix4d transform, Matrix3f normal) {
        renderObject(getCurrentMesh(), getCurrentSprite(), transform, normal, vertexConsumer, tickDelta);
    }

    Mesh getCurrentMesh() {
        return mesh;
    }

    Sprite getCurrentSprite() {
        return sprite;
    }
}
