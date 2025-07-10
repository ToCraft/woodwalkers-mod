package tocraft.walkers.traits;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;


@SuppressWarnings("unused")
public abstract class ShapeTrait<E extends LivingEntity> {
    public abstract ResourceLocation getId();

    public abstract MapCodec<? extends ShapeTrait<?>> codec();

    public boolean canBeRegisteredMultipleTimes() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public boolean iconMightDiffer() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public void renderIcon(RenderPipeline pipeline, GuiGraphics graphics, int x, int y, int width, int height) {

    }
}
