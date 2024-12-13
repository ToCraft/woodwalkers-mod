package tocraft.walkers.traits.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.traits.ShapeTrait;

public class StandOnFluidTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("stand_on_fluid");
    public static final MapCodec<StandOnFluidTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("fluid").forGetter(o -> o.fluidTagKey.location())
    ).apply(instance, instance.stable(fluid -> new StandOnFluidTrait<>(TagKey.create(Registries.FLUID, fluid)))));

    public final TagKey<Fluid> fluidTagKey;


    public StandOnFluidTrait(TagKey<Fluid> fluidTagKey) {
        this.fluidTagKey = fluidTagKey;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }

    @Contract(pure = true)
    @Environment(EnvType.CLIENT)
    @Override
    public @Nullable Item getItemIcon() {
        return Items.OAK_BOAT;
    }
}
