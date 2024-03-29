package immersive_particles.mixin;

import immersive_particles.core.ImmersiveParticleManager;
import immersive_particles.core.searcher.ParticleChunkManager;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(method = "getLeftText()Ljava/util/List;", at = @At("TAIL"))
    protected void immersiveParticles$injectGetLeftText(CallbackInfoReturnable<List<String>> cir) {
        List<String> value = cir.getReturnValue();
        value.add("Immersive Particles: %d, %d render drops, %d update drops, %d chunks queued".formatted(
                ImmersiveParticleManager.particleCount.get(),
                ImmersiveParticleManager.getRenderDrops(),
                ImmersiveParticleManager.getUpdateDrops(),
                ParticleChunkManager.getProcessing()));
    }
}
