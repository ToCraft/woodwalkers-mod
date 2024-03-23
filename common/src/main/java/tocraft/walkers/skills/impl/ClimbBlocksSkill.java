package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

import java.util.ArrayList;
import java.util.List;

public class ClimbBlocksSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("climb_blocks");
    public static final Codec<ClimbBlocksSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("horizontal_collision", true).forGetter(o -> o.horizontalCollision),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("valid_blocks", new ArrayList<>()).forGetter(o -> o.validBlocks.stream().map(BuiltInRegistries.BLOCK::getKey).toList()),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("invalid_blocks", new ArrayList<>()).forGetter(o -> o.invalidBlocks.stream().map(BuiltInRegistries.BLOCK::getKey).toList())
    ).apply(instance, instance.stable((horizontalCollision, validBlocksLocation, invalidBlocksLocation) -> {
        List<Block> validBlocks = new ArrayList<>();
        for (ResourceLocation resourceLocation : validBlocksLocation) {
            if (BuiltInRegistries.BLOCK.containsKey(resourceLocation)) {
                validBlocks.add(BuiltInRegistries.BLOCK.get(resourceLocation));
            }
        }
        List<Block> invalidBlocks = new ArrayList<>();
        for (ResourceLocation resourceLocation : invalidBlocksLocation) {
            if (BuiltInRegistries.BLOCK.containsKey(resourceLocation)) {
                validBlocks.add(BuiltInRegistries.BLOCK.get(resourceLocation));
            }
        }
        return new ClimbBlocksSkill<>(horizontalCollision, validBlocks, invalidBlocks);
    })));

    public final boolean horizontalCollision;
    public final List<Block> validBlocks;
    public final List<Block> invalidBlocks;

    public ClimbBlocksSkill() {
        this(true);
    }

    public ClimbBlocksSkill(boolean horizontalCollision) {
        this(horizontalCollision, new ArrayList<>(), new ArrayList<>());
    }

    public ClimbBlocksSkill(List<Block> validBlocks, List<Block> invalidBlocks) {
        this(false, validBlocks, invalidBlocks);

    }

    public ClimbBlocksSkill(boolean horizontalCollision, List<Block> validBlocks, List<Block> invalidBlocks) {
        this.horizontalCollision = horizontalCollision;
        this.validBlocks = validBlocks;
        this.invalidBlocks = invalidBlocks;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends ShapeSkill<?>> codec() {
        return CODEC;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public @Nullable TextureAtlasSprite getIcon() {
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(Blocks.VINE.defaultBlockState()).getParticleIcon();
    }
}
