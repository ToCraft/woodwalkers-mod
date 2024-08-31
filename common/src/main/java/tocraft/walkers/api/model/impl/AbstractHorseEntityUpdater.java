package tocraft.walkers.api.model.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import tocraft.walkers.api.model.EntityUpdater;
import tocraft.walkers.mixin.accessor.AbstractHorseAccessor;

@Environment(EnvType.CLIENT)
public class AbstractHorseEntityUpdater<H extends AbstractHorse> implements EntityUpdater<H> {
    @Override
    public void update(Player player, H horse) {
        ((AbstractHorseAccessor) horse).setEatAnim(0);
        ((AbstractHorseAccessor) horse).setEatAnimO(0);
        ((AbstractHorseAccessor) horse).setStandAnim(0);
        ((AbstractHorseAccessor) horse).setStandAnimO(0);
        ((AbstractHorseAccessor) horse).setMouthAnim(0);
        ((AbstractHorseAccessor) horse).setMouthAnimO(0);
    }
}
