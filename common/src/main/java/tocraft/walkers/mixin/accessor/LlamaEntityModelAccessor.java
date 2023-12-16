package tocraft.walkers.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.geom.ModelPart;

@Mixin(LlamaModel.class)
public interface LlamaEntityModelAccessor {
    @Accessor
    ModelPart getLeg1();
}
