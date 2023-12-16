package tocraft.walkers.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.model.RavagerModel;
import net.minecraft.client.model.geom.ModelPart;

@Mixin(RavagerModel.class)
public interface RavagerEntityModelAccessor {
    @Accessor
    ModelPart getLeg1();
}
