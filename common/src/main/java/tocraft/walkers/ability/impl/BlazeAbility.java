package tocraft.walkers.ability.impl;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;

public class BlazeAbility<T extends Mob> extends ShapeAbility<T> {

    @Override
    public void onUse(Player player, Mob shape, Level world) {
        SmallFireball smallFireball = new SmallFireball(
                world,
                player.getX(),
                player.getEyeY(),
                player.getZ(),
                player.getLookAngle().x,
                player.getLookAngle().y,
                player.getLookAngle().z
        );

        smallFireball.setOwner(player);
        world.addFreshEntity(smallFireball);
        world.playSound(null, player, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.BLAZE_POWDER;
    }
}
