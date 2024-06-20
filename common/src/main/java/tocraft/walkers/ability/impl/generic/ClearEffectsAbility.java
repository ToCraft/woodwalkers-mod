package tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

public class ClearEffectsAbility<T extends Mob> extends GenericShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("clear_effects");
    public static final Codec<ClearEffectsAbility<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.stable(new ClearEffectsAbility<>()));

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends GenericShapeAbility<?>> codec() {
        return CODEC;
    }

    @Override
    public void onUse(Player player, T shape, Level world) {
        player.removeAllEffects();
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.MILK_BUCKET;
    }
}
