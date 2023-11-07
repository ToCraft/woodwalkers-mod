package tocraft.walkers.ability.impl;

import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import tocraft.walkers.ability.ShapeAbility;

public class CreeperAbility extends ShapeAbility<Creeper> {

    @Override
    public void onUse(Player player, Creeper shape, Level world) {
        world.explode(player, player.getX(), player.getY(), player.getZ(), 3.0f, ExplosionInteraction.NONE);
    }

    @Override
    public Item getIcon() {
        return Items.TNT;
    }
}
