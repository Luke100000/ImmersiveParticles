package immersive_particles.fabric.resources;

import immersive_particles.resources.ParticleManagerLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.util.Identifier;

public class FabricParticleManagerLoader extends ParticleManagerLoader implements IdentifiableResourceReloadListener {
    @Override
    public Identifier getFabricId() {
        return ID;
    }
}
