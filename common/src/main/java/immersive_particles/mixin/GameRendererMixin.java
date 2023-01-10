package immersive_particles.mixin;

import immersive_particles.Shaders;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "loadShaders(Lnet/minecraft/resource/ResourceManager;)V", at=@At("HEAD"))
    public void immersiveParticles$injectLoadShaders(ResourceManager manager, CallbackInfo ci) {
        Shaders.LoadShaders(manager);
    }
}
