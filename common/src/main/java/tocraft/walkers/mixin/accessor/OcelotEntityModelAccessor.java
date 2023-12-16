package tocraft.walkers.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelPart;

@Mixin(OcelotModel.class)
public interface OcelotEntityModelAccessor {
    @Accessor
    ModelPart getFrontLegR();
}
