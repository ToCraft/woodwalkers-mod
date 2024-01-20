package tocraft.walkers.ability.impl;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;

public class EndermanAbility extends ShapeAbility<EnderMan> {

    @Override
    public void onUse(Player player, EnderMan shape, Level world) {
        HitResult lookingAt = player.pick(Walkers.CONFIG.endermanAbilityTeleportDistance, 0, true);
        player.teleportTo(lookingAt.getLocation().x, lookingAt.getLocation().y, lookingAt.getLocation().z);
        player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1, 1);
    }

    @Override
    public Item getIcon() {
        return Items.ENDER_PEARL;
    }
}
