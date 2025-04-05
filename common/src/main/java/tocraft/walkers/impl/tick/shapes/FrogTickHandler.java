package tocraft.walkers.impl.tick.shapes;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import tocraft.walkers.api.WalkersTickHandler;

public class FrogTickHandler implements WalkersTickHandler<Frog> {

    @Override
    public void tick(Player player, Frog frog) {
        if (player.level().isClientSide) {
            boolean isInWater = player.isInWater();

            boolean walk = player.onGround() && player.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6 && !isInWater;
            boolean swim = player.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6 && isInWater;

            // Jumping
            if (!player.onGround() && !swim && !walk && !isInWater) {
                frog.jumpAnimationState.startIfStopped(frog.tickCount);
            } else {
                frog.jumpAnimationState.stop();
            }

            // Swimming
            if (swim) {
                frog.swimIdleAnimationState.stop();
            } else if (isInWater) {
                frog.swimIdleAnimationState.startIfStopped(frog.tickCount);
            } else {
                frog.swimIdleAnimationState.stop();
            }

            // Random croaking
            if (player.level().random.nextDouble() <= 0.001) {
                frog.croakAnimationState.start(player.tickCount);
            }

            // Tongue
            if (player.swinging) {
                frog.tongueAnimationState.startIfStopped(player.tickCount);
            } else {
                frog.tongueAnimationState.stop();
            }
        } else {
            // Buffs - jump boost
            player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 20 * 2, 2, true, false));
        }
    }
}
