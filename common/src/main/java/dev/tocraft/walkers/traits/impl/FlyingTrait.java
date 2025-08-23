package dev.tocraft.walkers.traits.impl;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.serialization.Codec;
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

public class FlyingTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("flying");
    public static final MapCodec<FlyingTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("slow_falling", true).forGetter(o -> o.slowFalling)
    ).apply(instance, instance.stable(FlyingTrait::new)));

    public FlyingTrait() {
        this.slowFalling = false;
    }

    public FlyingTrait(boolean slowFalling) {
        this.slowFalling = slowFalling;
    }

    public final boolean slowFalling;

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
        ItemStack stack = new ItemStack(Items.ELYTRA);
        graphics.renderItem(stack, x, y);
        return true;
    }
}
