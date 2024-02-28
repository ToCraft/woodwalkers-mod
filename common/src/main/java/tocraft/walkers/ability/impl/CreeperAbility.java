package tocraft.walkers.ability.impl;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import tocraft.walkers.ability.ShapeAbility;

public class CreeperAbility<T extends Mob> extends ShapeAbility<T> {

    @Override
    public void onUse(Player player, T shape, Level world) {
        world.explode(player, player.getX(), player.getY(), player.getZ(), 3.0f, ExplosionInteraction.NONE);
    }

    @Override
    public Item getIcon() {
        return Items.TNT;
    }

    @Override
    public int getDefaultCooldown() {
        return 100;
    }
}
