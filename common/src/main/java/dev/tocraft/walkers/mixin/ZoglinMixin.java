package dev.tocraft.walkers.mixin;

import dev.tocraft.walkers.api.PlayerHostility;
import dev.tocraft.walkers.impl.PlayerDataProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zoglin.class)
public class ZoglinMixin {
    @Inject(method = "isTargetable", at = @At("RETURN"), cancellable = true)
    private void fixTarget(ServerLevel level, LivingEntity entity, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && entity instanceof Player player && !PlayerHostility.hasHostility(player) && ((PlayerDataProvider) player).walkers$getCurrentShape() instanceof Zoglin) {
            cir.setReturnValue(false);
        }
    }
}
