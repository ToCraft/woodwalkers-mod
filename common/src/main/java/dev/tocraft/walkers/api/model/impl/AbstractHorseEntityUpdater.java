package dev.tocraft.walkers.api.model.impl;

import dev.tocraft.walkers.api.model.EntityUpdater;
import dev.tocraft.walkers.mixin.accessor.AbstractHorseAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public class AbstractHorseEntityUpdater<H extends AbstractHorse> implements EntityUpdater<H> {
    @Override
    public void update(Player player, H horse) {
        // block some animations
        ((AbstractHorseAccessor) horse).setEatAnim(0);
        ((AbstractHorseAccessor) horse).setEatAnimO(0);
        ((AbstractHorseAccessor) horse).setMouthAnim(0);
        ((AbstractHorseAccessor) horse).setMouthAnimO(0);

        // stand while jumping
        ((AbstractHorseAccessor) horse).setStandAnimO(((AbstractHorseAccessor) horse).getStandAnim());
        if (player.isJumping()) {
            horse.setStanding(200); // random value I like, prob. a really random way of fixing this
        }
        if (((AbstractHorseAccessor) horse).getStandCounter() <= 200) {
            if (player.onGround()) {
                ((AbstractHorseAccessor) horse).setStandAnim(Math.max(((0.8F * ((AbstractHorseAccessor) horse).getStandAnim() * ((AbstractHorseAccessor) horse).getStandAnim() * ((AbstractHorseAccessor) horse).getStandAnim() - ((AbstractHorseAccessor) horse).getStandAnim()) * 0.6F - 0.05F), 0));
                horse.setStanding(((AbstractHorseAccessor) horse).getStandCounter() - 1);
                if (((AbstractHorseAccessor) horse).getStandCounter() < 100) {
                    horse.clearStanding();
                }
            } else {
                ((AbstractHorseAccessor) horse).setStandAnim(Math.min(((AbstractHorseAccessor) horse).getStandAnim() + ((1.0F - ((AbstractHorseAccessor) horse).getStandAnim()) * 0.4F + 0.05F), 1));
            }
        }
    }
}
