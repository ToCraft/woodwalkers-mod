package tocraft.walkers.ability.impl;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.WalkersAbility;

public class CowAbility extends WalkersAbility<Cow> {

    @Override
    public void onUse(Player player, Cow shape, Level world) {
        player.removeAllEffects();
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.MILK_BUCKET;
    }
}
