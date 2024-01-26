package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WalkAnimationState.class)
public interface LimbAnimatorAccessor {
    @Accessor("speedOld")
    float getPrevSpeed();

    @Accessor("position")
    void setPos(float pos);

    @Accessor("speedOld")
    void setPrevSpeed(float prevSpeed);
}