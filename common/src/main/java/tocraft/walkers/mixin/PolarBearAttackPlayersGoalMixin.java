package tocraft.walkers.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;

@Mixin(PolarBear.PolarBearAttackPlayersGoal.class)
public class PolarBearAttackPlayersGoalMixin extends NearestAttackableTargetGoal<Player> {

    public PolarBearAttackPlayersGoalMixin(Mob mob, Class<Player> targetType, boolean mustSee) {
        super(mob, targetType, mustSee);
    }

    @Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
    private void onCanUse(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (this.target instanceof Player player && PlayerShape.getCurrentShape(player) instanceof PolarBear) {
                cir.setReturnValue(false);
            }
        }
    }
}
