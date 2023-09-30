package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Parrot.class)
public interface ParrotEntityAccessor {
    @Invoker
    void callCalculateFlapping();
}
