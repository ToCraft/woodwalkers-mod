package tocraft.walkers.ability.impl;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;

public class GhastAbility<T extends Mob> extends ShapeAbility<T> {

    @Override
    public void onUse(Player player, T shape, Level world) {
        LargeFireball fireball = new LargeFireball(
                world,
                player,
                player.getLookAngle().x,
                player.getLookAngle().y,
                player.getLookAngle().z,
                2
        );

        fireball.moveTo(fireball.getX(), fireball.getY() + 1.75, fireball.getZ(), fireball.getYRot(), fireball.getXRot());
        fireball.absMoveTo(fireball.getX(), fireball.getY(), fireball.getZ());
        world.addFreshEntity(fireball);
        world.playSound(null, player, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
        world.playSound(null, player, SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 10.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.FIRE_CHARGE;
    }

    @Override
    public int getDefaultCooldown() {
        return 60;
    }
}
