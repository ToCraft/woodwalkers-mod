package tocraft.walkers.mixin.accessor;

import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SquidModel.class)
public interface SquidEntityModelAccessor {
    @Accessor
    ModelPart[] getTentacles();
}
