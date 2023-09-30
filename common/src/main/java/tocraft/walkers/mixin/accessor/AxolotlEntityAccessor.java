package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.axolotl.Axolotl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Axolotl.class)
public interface AxolotlEntityAccessor {

    @Invoker
    void callSetVariant(Axolotl.Variant variant);
}
