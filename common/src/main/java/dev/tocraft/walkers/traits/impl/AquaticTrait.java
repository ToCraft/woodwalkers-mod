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

import java.util.Optional;

public class AquaticTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("aquatic");
    public static final MapCodec<AquaticTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.INT.optionalFieldOf("is_aquatic").forGetter(o -> Optional.empty()),
            Codec.BOOL.optionalFieldOf("isAquatic", true).forGetter(o -> o.isAquatic),
            Codec.BOOL.optionalFieldOf("isLand", false).forGetter(o -> o.isLand)
    ).apply(instance, instance.stable((i, isAquatic, isLand) -> {
        if (i.isPresent()) {
            switch (i.get()) {
                case 0 -> {
                    return new AquaticTrait<>(true, false);
                }
                case 1 -> {
                    return new AquaticTrait<>(true, true);
                }
                case 2 -> {
                    return new AquaticTrait<>(false, true);
                }
            }
        }
        return new AquaticTrait<>(isAquatic, isLand);
    })));

    public final boolean isAquatic;
    public final boolean isLand;

    public AquaticTrait(boolean isAquatic, boolean isLand) {
        this.isAquatic = isAquatic;
        this.isLand = isLand;
    }

    public AquaticTrait(boolean isAquatic) {
        this(isAquatic, !isAquatic);
    }

    public AquaticTrait() {
        this(true, false);
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
        ItemStack stack = new ItemStack(Items.HEART_OF_THE_SEA);
        graphics.renderItem(stack, x, y);
        return true;
    }
}
