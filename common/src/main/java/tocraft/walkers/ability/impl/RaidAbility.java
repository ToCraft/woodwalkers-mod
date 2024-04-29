package tocraft.walkers.ability.impl;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;

public class RaidAbility<T extends Mob> extends ShapeAbility<T> {

    @Override
    public void onUse(Player player, Mob shape, Level world) {
        if (world instanceof ServerLevel serverLevel) {
            serverLevel.getRaids().createOrExtendRaid((ServerPlayer) player);
            player.playSound(SoundEvents.RAID_HORN, 1.0F, 1.0F);
        }
    }

    @Override
    public int getDefaultCooldown() {
        return 2400;
    }

    @Override
    public Item getIcon() {
        return Items.CROSSBOW;
    }
}
