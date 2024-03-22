package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

public class StandOnFluidSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("stand_on_fluid");
    public static final Codec<StandOnFluidSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("fluid").forGetter(o -> o.fluidTagKey.location())
    ).apply(instance, instance.stable(fluid -> new StandOnFluidSkill<>(TagKey.create(Registries.FLUID, fluid)))));

    public final TagKey<Fluid> fluidTagKey;


    public StandOnFluidSkill(TagKey<Fluid> fluidTagKey) {
        this.fluidTagKey = fluidTagKey;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends ShapeSkill<?>> codec() {
        return CODEC;
    }
}
