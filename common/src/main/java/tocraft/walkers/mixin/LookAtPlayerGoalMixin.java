package tocraft.walkers.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;

@Mixin(LookAtPlayerGoal.class)
public class LookAtPlayerGoalMixin {
    @Shadow
    @Nullable
    protected Entity lookAt;
    @Shadow
    @Final
    protected Mob mob;

    @Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
    private void onCanUse(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            // prevent mobs from looking at the player
            cir.setReturnValue(!(this.lookAt instanceof Player player && PlayerShape.getCurrentShape(player) != null && PlayerShape.getCurrentShape(player).getType() == this.mob.getType()));
        }
    }
}
