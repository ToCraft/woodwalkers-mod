package tocraft.walkers.ability.impl;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;

public class ChickenAbility<T extends Mob> extends ShapeAbility<T> {

    @Override
    public void onUse(Player player, Mob shape, Level world) {
        player.spawnAtLocation(Items.EGG);

        // Play SFX
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHICKEN_EGG, player.getSoundSource(), 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F);
    }

    @Override
    public Item getIcon() {
        return Items.EGG;
    }

    @Override
    public int getDefaultCooldown() {
        return 1200;
    }
}
