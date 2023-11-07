package tocraft.walkers.ability.impl;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;

public class MushroomCowAbility extends ShapeAbility<MushroomCow> {

    @Override
    public void onUse(Player player, MushroomCow shape, Level world) {
        player.getFoodData().eat(6, 0.1F);

        world.playSound(null, player, SoundEvents.SHEEP_STEP, SoundSource.PLAYERS, 1.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.MUSHROOM_STEW;
    }
}
