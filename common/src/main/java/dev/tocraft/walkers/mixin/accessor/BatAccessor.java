package dev.tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.ambient.Bat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Bat.class)
public interface BatAccessor {
    @Invoker
    void callSetupAnimationStates();
}
