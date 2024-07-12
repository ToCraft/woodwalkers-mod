//#if MC>1182
package tocraft.walkers.impl.tick.shapes;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import tocraft.craftedcore.patched.CEntity;
import tocraft.walkers.api.WalkersTickHandler;

public class FrogTickHandler implements WalkersTickHandler<Frog> {

    @Override
    public void tick(Player player, Frog frog) {
        if (CEntity.level(player).isClientSide) {
            boolean walk = CEntity.isOnGround(player) && player.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6 && !player.isInWaterOrBubble();
            boolean swim = player.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6 && player.isInWaterOrBubble();

            // Jumping
            if (!CEntity.isOnGround(player) && !swim && !walk && !player.isInWaterOrBubble()) {
                frog.jumpAnimationState.startIfStopped(frog.tickCount);
            } else {
                frog.jumpAnimationState.stop();
            }

            // Swimming
            if (swim) {
                frog.swimIdleAnimationState.stop();
            } else if (player.isInWaterOrBubble()) {
                frog.swimIdleAnimationState.startIfStopped(frog.tickCount);
            } else {
                frog.swimIdleAnimationState.stop();
            }

            // Random croaking
            if (CEntity.level(player).random.nextDouble() <= 0.001) {
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
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 20 * 2, 2, true, false));
        }
    }
}
//#endif
