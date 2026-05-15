package dev.tocraft.walkers.mixin;

import dev.tocraft.walkers.api.PlayerHostility;
import dev.tocraft.walkers.impl.PlayerDataProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zoglin.class)
public class ZoglinMixin {
    @Inject(method = "lambda$findNearestValidAttackTarget$0", at = @At("RETURN"), cancellable = true)
    private static void fixTarget(ServerLevel level, Mob mob, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && entity instanceof Player player && !PlayerHostility.hasHostility(player) && ((PlayerDataProvider) player).walkers$getCurrentShape() instanceof Zoglin) {
            cir.setReturnValue(false);
        }
    }
}
