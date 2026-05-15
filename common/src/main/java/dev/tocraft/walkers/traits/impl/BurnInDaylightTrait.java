package dev.tocraft.walkers.traits.impl;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.traits.ShapeTrait;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class BurnInDaylightTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final Identifier ID = Walkers.id("burn_in_daylight");
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
    public Identifier getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean renderIcon(RenderPipeline pipeline, @NotNull GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(TextureAtlas.LOCATION_BLOCKS).getSprite(Identifier.withDefaultNamespace("block/fire_0"));
        graphics.blitSprite(pipeline, sprite, x, y, width, height);
        return true;
    }
}
