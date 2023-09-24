package tocraft.walkers.mixin.accessor;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpiderModel.class)
public interface SpiderEntityModelAccessor {
    @Accessor
    ModelPart getRightFrontLeg();
}
