//#if MC>=1203
package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Bat.class)
public interface BatAccessor {
    @Invoker
    void callSetupAnimationStates();
}
//#endif
