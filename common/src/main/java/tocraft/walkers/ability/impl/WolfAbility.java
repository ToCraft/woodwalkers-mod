package tocraft.walkers.ability.impl;

import tocraft.walkers.ability.WalkersAbility;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class WolfAbility extends WalkersAbility<WolfEntity> {

    @Override
    public void onUse(PlayerEntity player, WolfEntity shape, World world) {
        if (shape.hasAngerTime())
            shape.stopAnger();
        else
            shape.chooseRandomAngerTime();
    }

    @Override
    public Item getIcon() {
        return Items.RED_DYE;
    }
}
