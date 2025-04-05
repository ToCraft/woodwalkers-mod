package tocraft.walkers.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Frog.class)
public interface FrogAccessor {
    @Invoker
    void callSetVariant(Holder<FrogVariant> variant);
}
