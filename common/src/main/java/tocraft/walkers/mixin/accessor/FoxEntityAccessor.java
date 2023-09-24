package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Fox.class)
public interface FoxEntityAccessor {

    @Invoker
    void callSetVariant(Fox.Type type);
}
