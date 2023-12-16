package tocraft.walkers.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelPart;

@Mixin(SpiderModel.class)
public interface SpiderEntityModelAccessor {
    @Accessor
    ModelPart getLeg1();
}
