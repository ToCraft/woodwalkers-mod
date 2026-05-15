package dev.tocraft.walkers.traits;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;


@SuppressWarnings("unused")
public abstract class ShapeTrait<E extends LivingEntity> {
    public abstract Identifier getId();

    public abstract MapCodec<? extends ShapeTrait<?>> codec();

    public boolean canBeRegisteredMultipleTimes() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public boolean iconMightDiffer() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public boolean renderIcon(RenderPipeline pipeline, GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        return false;
    }
}
