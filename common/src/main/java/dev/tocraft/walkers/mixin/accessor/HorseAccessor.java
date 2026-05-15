package dev.tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Variant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Horse.class)
public interface HorseAccessor {
    @Invoker
    void callSetVariant(Variant variant);
}
