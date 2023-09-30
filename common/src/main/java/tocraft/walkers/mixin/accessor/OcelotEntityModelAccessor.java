package tocraft.walkers.mixin.accessor;

import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OcelotModel.class)
public interface OcelotEntityModelAccessor {
    @Accessor
    ModelPart getRightFrontLeg();
}
