package tocraft.walkers.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.FlightHelper;

@Mixin(PlayerAdvancements.class)
public class PlayerAdvancementTrackerMixin {

    @Shadow
    private ServerPlayer player;

    @Inject(
            method = "award",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementRewards;grant(Lnet/minecraft/server/level/ServerPlayer;)V")
    )
    private void refreshFlight(AdvancementHolder advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (Walkers.hasFlyingPermissions(player)) {
            FlightHelper.grantFlightTo(player);
            player.getAbilities().setFlyingSpeed(Walkers.CONFIG.flySpeed);
            player.onUpdateAbilities();
        }
    }
}
