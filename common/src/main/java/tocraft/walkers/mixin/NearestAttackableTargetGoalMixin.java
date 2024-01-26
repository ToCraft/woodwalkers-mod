package tocraft.walkers.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;

@Mixin(NearestAttackableTargetGoal.class)
public class NearestAttackableTargetGoalMixin<T extends LivingEntity> extends TargetGoal {

    @Shadow
    @Nullable
    protected LivingEntity target;

    @Shadow
    @Final
    protected Class<T> targetType;

    public NearestAttackableTargetGoalMixin(Mob mob, boolean mustSee) {
        super(mob, mustSee);
    }

    @Inject(method = "findTarget", at = @At("RETURN"))
    protected void onFindTarget(CallbackInfo ci) {
        Player nearestPlayer = this.mob.level.getNearestPlayer(mob, this.getFollowDistance());
        if (nearestPlayer != null && targetType.isInstance(PlayerShape.getCurrentShape(nearestPlayer))) {
            Walkers.LOGGER.warn("TRUEEE");
            this.target = nearestPlayer;
        } else
            Walkers.LOGGER.warn("NEEE");
    }

    @Override
    public boolean canUse() {
        return false;
    }
}
