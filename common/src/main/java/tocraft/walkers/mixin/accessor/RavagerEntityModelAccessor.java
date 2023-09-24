package tocraft.walkers.mixin.accessor;

import net.minecraft.client.model.RavagerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RavagerModel.class)
public interface RavagerEntityModelAccessor {
    @Accessor
    ModelPart getRightFrontLeg();
}
