package tocraft.walkers.ability.impl;

import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.impl.SonicBoomUser;

public class WardenAbility extends ShapeAbility<Warden> {

    @Override
    public void onUse(Player player, Warden shape, Level world) {
        ((SonicBoomUser) player).shape$ability_startSonicBoom();
    }

    @Override
    public Item getIcon() {
        return Items.ECHO_SHARD;
    }
}
