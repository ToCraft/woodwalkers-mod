package dev.tocraft.walkers.traits.impl;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.traits.ShapeTrait;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

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

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderIcon(RenderPipeline pipeline, @NotNull GuiGraphics graphics, int x, int y, int width, int height) {
        ItemStack stack = new ItemStack(Items.OAK_BOAT);
        graphics.renderItem(stack, x, y);
        return true;
    }
}
