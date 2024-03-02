package tocraft.walkers.ability.impl;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;

public class EndermanAbility<T extends Mob> extends ShapeAbility<T> {

    @Override
    public void onUse(Player player, T shape, Level world) {
        HitResult lookingAt = player.pick(Walkers.CONFIG.endermanAbilityTeleportDistance, 0, true);
        player.teleportTo(lookingAt.getLocation().x, lookingAt.getLocation().y, lookingAt.getLocation().z);
        player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1, 1);
    }

    @Override
    public Item getIcon() {
        return Items.ENDER_PEARL;
    }

    @Override
    public int getDefaultCooldown() {
        return 100;
    }
}
