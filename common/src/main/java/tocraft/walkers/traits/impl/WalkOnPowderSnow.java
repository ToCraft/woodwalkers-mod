package tocraft.walkers.traits.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.traits.ShapeTrait;

public class WalkOnPowderSnow<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("walk_on_powder_snow");
    public static final MapCodec<WalkOnPowderSnow<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.stable(new WalkOnPowderSnow<>()));

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }

    @Override
    public boolean canBeRegisteredMultipleTimes() {
        return false;
    }

    @Contract(pure = true)
    @Environment(EnvType.CLIENT)
    @Override
    public @Nullable Item getItemIcon() {
        return Items.SNOWBALL;
    }
}
