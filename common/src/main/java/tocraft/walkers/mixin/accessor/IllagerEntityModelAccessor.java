package tocraft.walkers.mixin.accessor;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IllagerModel.class)
public interface IllagerEntityModelAccessor {
    @Accessor
    ModelPart getRightArm();
}
