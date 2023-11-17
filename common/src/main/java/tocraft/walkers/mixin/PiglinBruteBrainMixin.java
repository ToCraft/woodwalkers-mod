package tocraft.walkers.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBruteAi;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;

@Mixin(PiglinBruteAi.class)
public class PiglinBruteBrainMixin {

    /**
     * @author ToCraft
     *
     * @reason method_30255 is the desugared lambda used by method_30249 that searches for a nearby player to aggro on.
     * This mixin modifies the search logic to exclude players disguised as anything besides a Wither Skeleton or Wither.
     */
    @Inject( method = "method_30255", at = @At("HEAD"), expect = 0, cancellable = true)
    private static void shape$method_30249FilterLambdaIntermediary(AbstractPiglin abstractPiglinEntity, LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if(livingEntity instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if(shape != null && !(shape instanceof WitherSkeleton) && !(shape instanceof WitherBoss)) {
                cir.setReturnValue(false);
            }
        }
    }
}