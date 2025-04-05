package tocraft.walkers.traits.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.traits.ShapeTrait;

public class BurnInDaylightTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("burn_in_daylight");
    public static final MapCodec<BurnInDaylightTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("burn_in_moonlight_instead", false).forGetter(o -> o.burnInMoonlightInstead)
    ).apply(instance, instance.stable(BurnInDaylightTrait::new)));

    public BurnInDaylightTrait() {
        this.burnInMoonlightInstead = false;
    }

    public BurnInDaylightTrait(boolean burnInMoonlightInstead) {
        this.burnInMoonlightInstead = burnInMoonlightInstead;
    }

    public final boolean burnInMoonlightInstead;

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public @Nullable TextureAtlasSprite getIcon() {
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(Blocks.FIRE.defaultBlockState()).particleIcon();
    }
}
