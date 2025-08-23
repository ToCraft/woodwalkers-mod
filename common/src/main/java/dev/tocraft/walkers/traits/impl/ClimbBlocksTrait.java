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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClimbBlocksTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("climb_blocks");
    public static final MapCodec<ClimbBlocksTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("horizontal_collision", true).forGetter(o -> o.horizontalCollision),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("valid_blocks", new ArrayList<>()).forGetter(o -> o.validBlocks.stream().map(BuiltInRegistries.BLOCK::getKey).toList()),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("invalid_blocks", new ArrayList<>()).forGetter(o -> o.invalidBlocks.stream().map(BuiltInRegistries.BLOCK::getKey).toList())
    ).apply(instance, instance.stable((horizontalCollision, validBlocksLocation, invalidBlocksLocation) -> {
        List<Block> validBlocks = new ArrayList<>();
        for (ResourceLocation resourceLocation : validBlocksLocation) {
            if (BuiltInRegistries.BLOCK.containsKey(resourceLocation)) {
                validBlocks.add(BuiltInRegistries.BLOCK.get(resourceLocation).orElseThrow().value());
            }
        }
        List<Block> invalidBlocks = new ArrayList<>();
        for (ResourceLocation resourceLocation : invalidBlocksLocation) {
            if (BuiltInRegistries.BLOCK.containsKey(resourceLocation)) {
                validBlocks.add(BuiltInRegistries.BLOCK.get(resourceLocation).orElseThrow().value());
            }
        }
        return new ClimbBlocksTrait<>(horizontalCollision, validBlocks, invalidBlocks);
    })));

    public final boolean horizontalCollision;
    public final List<Block> validBlocks;
    public final List<Block> invalidBlocks;

    public ClimbBlocksTrait() {
        this(true);
    }

    public ClimbBlocksTrait(boolean horizontalCollision) {
        this(horizontalCollision, new ArrayList<>(), new ArrayList<>());
    }

    public ClimbBlocksTrait(List<Block> validBlocks, List<Block> invalidBlocks) {
        this(false, validBlocks, invalidBlocks);

    }

    public ClimbBlocksTrait(boolean horizontalCollision, List<Block> validBlocks, List<Block> invalidBlocks) {
        this.horizontalCollision = horizontalCollision;
        this.validBlocks = validBlocks;
        this.invalidBlocks = invalidBlocks;
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
        ItemStack stack = new ItemStack(Items.VINE);
        graphics.renderItem(stack, x, y);
        return true;
    }
}
