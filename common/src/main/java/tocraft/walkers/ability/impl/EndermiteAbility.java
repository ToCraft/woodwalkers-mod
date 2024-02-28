package tocraft.walkers.ability.impl;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;

public class EndermiteAbility<T extends Mob> extends ShapeAbility<T> {

    @Override
    public void onUse(Player player, T shape, Level world) {
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        for (int i = 0; i < 16; ++i) {
            // Pick a random location nearby to teleport to.
            double g = player.getX() + (player.getRandom().nextDouble() - 0.5D) * 16.0D;
            double h = Mth.clamp(player.getY() + (double) (player.getRandom().nextInt(16) - 8), 0.0D, world.getHeight() - 1);
            double j = player.getZ() + (player.getRandom().nextDouble() - 0.5D) * 16.0D;

            // Cancel vehicle/riding mechanics.
            if (player.isPassenger()) {
                player.stopRiding();
            }

            // Teleport the player and play sound FX if it succeeds.
            if (player.randomTeleport(g, h, j, true)) {
                SoundEvent soundEvent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                world.playSound(null, x, y, z, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.playSound(soundEvent, 1.0F, 1.0F);
                break;
            }
        }
    }

    @Override
    public Item getIcon() {
        return Items.CHORUS_FRUIT;
    }
}
