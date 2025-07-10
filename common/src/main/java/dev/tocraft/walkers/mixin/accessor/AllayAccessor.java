package dev.tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.allay.Allay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Allay.class)
public interface AllayAccessor {
    @Accessor
    void setHoldingItemAnimationTicks0(float holdingItemAnimationTicks0);

    @Accessor
    float getHoldingItemAnimationTicks();

    @Accessor
    void setHoldingItemAnimationTicks(float holdingItemAnimationTicks);
}
