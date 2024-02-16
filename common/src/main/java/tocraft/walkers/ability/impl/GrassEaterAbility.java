package tocraft.walkers.ability.impl;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;

public abstract class GrassEaterAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public int eatTick = 0;

    @Override
    public void onUse(Player player, T shape, Level world) {
        eatGrass();
    }

    public void eatGrass() {
        eatTick = Mth.positiveCeilDiv(40, 2);
    }
}
