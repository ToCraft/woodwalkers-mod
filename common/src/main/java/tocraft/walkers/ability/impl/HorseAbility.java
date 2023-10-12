package tocraft.walkers.ability.impl;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.WalkersAbility;

public class HorseAbility <T extends Mob> extends WalkersAbility<T> {

    @Override
    public void onUse(Player player, T shape, Level world) {
    	int level = randInt(1, 3);
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, this.getCooldown(shape) / 2, level, true, false));
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.HORSE_JUMP, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.IRON_HORSE_ARMOR;
    }
    
    // Why isn't this already implemented in Java's Code?!?
    private static int randInt(int min, int max) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }
}
