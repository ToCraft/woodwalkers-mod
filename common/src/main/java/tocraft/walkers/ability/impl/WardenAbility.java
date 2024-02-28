package tocraft.walkers.ability.impl;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.impl.SonicBoomUser;

public class WardenAbility<T extends Mob> extends ShapeAbility<T> {

    @Override
    public void onUse(Player player, T shape, Level world) {
        ((SonicBoomUser) player).shape$ability_startSonicBoom();
    }

    @Override
    public Item getIcon() {
        return Items.ECHO_SHARD;
    }

    @Override
    public int getDefaultCooldown() {
        return 200;
    }
}
