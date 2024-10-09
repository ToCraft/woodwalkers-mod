package tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

public class ClearEffectsAbility<T extends LivingEntity> extends GenericShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("clear_effects");
    public static final MapCodec<ClearEffectsAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.stable(new ClearEffectsAbility<>()));

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends GenericShapeAbility<?>> codec() {
        return CODEC;
    }

    @Override
    public void onUse(Player player, T shape, Level world) {
        super.onUse(player, shape, world);
        player.removeAllEffects();
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.MILK_BUCKET;
    }
}
