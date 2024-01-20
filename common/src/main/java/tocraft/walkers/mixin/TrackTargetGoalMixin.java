package tocraft.walkers.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetGoal.class)
public abstract class TrackTargetGoalMixin {
    @Shadow
    @Final
    protected Mob mob;

    @Shadow
    public abstract void stop();

    @Inject(method = "canContinueToUse", at = @At("RETURN"), cancellable = true)
    protected void shape_shouldContinue(CallbackInfoReturnable<Boolean> cir) {
        // NO-OP
    }
}
