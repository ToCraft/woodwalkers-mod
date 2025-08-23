package dev.tocraft.walkers.traits.impl;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.traits.ShapeTrait;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class NoPhysicsTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("no_physics");
    public static final MapCodec<NoPhysicsTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.stable(new NoPhysicsTrait<>()));

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

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderIcon(RenderPipeline pipeline, @NotNull GuiGraphics graphics, int x, int y, int width, int height) {
        ItemStack stack = new ItemStack(Items.ENDER_EYE);
        graphics.renderItem(stack, x, y);
        return true;
    }
}
