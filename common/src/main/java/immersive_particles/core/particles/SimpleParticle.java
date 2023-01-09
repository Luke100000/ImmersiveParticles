package immersive_particles.core.particles;

import immersive_particles.Main;
import immersive_particles.core.ImmersiveParticleType;
import immersive_particles.core.SpawnLocation;
import immersive_particles.util.obj.Mesh;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.JsonHelper;

public class SimpleParticle extends ImmersiveParticle {
    private final Sprite sprite;
    private final Mesh mesh;

    public SimpleParticle(ImmersiveParticleType type, SpawnLocation location) {
        super(type, location);

        this.sprite = type.getSprites().get(random.nextInt(type.getSprites().size()));

        mesh = getMesh(Main.locate(JsonHelper.getString(type.behavior, "object")), JsonHelper.getString(type.behavior, "mesh"));
    }

    @Override
    Mesh getCurrentMesh() {
        return mesh;
    }

    @Override
    Sprite getCurrentSprite() {
        return sprite;
    }


}
