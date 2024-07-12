package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Fox.class)
public interface FoxEntityAccessor {
    //#if MC>1182
    @Invoker
    void callSetVariant(Fox.Type type);
    
    //#else
    //$$ @Invoker
    //$$ void callSetFoxType(Fox.Type type);
    //#endif
}
