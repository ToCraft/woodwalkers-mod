package tocraft.walkers.mixin.integrations.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnresolvedMixinReference")
@Pseudo
@Environment(EnvType.CLIENT)
@Mixin(targets = "net.bettercombat.compatibility.CompatibilityFlags", remap = false)
public class BetterCombatMixin {
    @Inject(method = "firstPersonRender", at = @At("RETURN"), cancellable = true)
    private static void onFirstPersonRenderCall(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
