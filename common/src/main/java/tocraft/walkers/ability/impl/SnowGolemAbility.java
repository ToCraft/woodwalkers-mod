package tocraft.walkers.ability.impl;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;

public class SnowGolemAbility<T extends Mob> extends ShapeAbility<T> {

    @Override
    public void onUse(Player player, T shape, Level world) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        if (!world.isClientSide) {
            for (int i = 0; i < 10; i++) {
                Snowball snowballEntity = new Snowball(world, player);
                snowballEntity.setItem(new ItemStack(Items.SNOWBALL));
                snowballEntity.shootFromRotation(player, player.getXRot() + world.random.nextInt(10) - 5, player.getYRot() + world.random.nextInt(10) - 5, 0.0F, 1.5F, 1.0F);
                world.addFreshEntity(snowballEntity);
            }
        }
    }

    @Override
    public Item getIcon() {
        return Items.SNOWBALL;
    }

    @Override
    public int getDefaultCooldown() {
        return 10;
    }
}
