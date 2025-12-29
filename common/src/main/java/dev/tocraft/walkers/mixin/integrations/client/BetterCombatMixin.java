package dev.tocraft.walkers.mixin.integrations.client;

import dev.tocraft.walkers.api.PlayerShape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
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
    @Inject(method = "firstPersonRender", at = @At("RETURN"), cancellable = true, require = 0)
    private static void onFirstPersonRenderCall(CallbackInfoReturnable<Boolean> cir) {
        Player player = Minecraft.getInstance().player;
        if (player != null && PlayerShape.getCurrentShape(player) != null) {
            cir.setReturnValue(false);
        }
    }
}
