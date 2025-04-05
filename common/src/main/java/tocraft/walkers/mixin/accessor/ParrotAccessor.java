package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Rabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Parrot.class)
public interface ParrotAccessor {
    @Invoker
    void callSetVariant(Parrot.Variant variant);
}
