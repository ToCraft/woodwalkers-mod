package tocraft.walkers.mixin.client.accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(WalkAnimationState.class)
public interface LimbAnimatorAccessor {
    @Accessor("speedOld")
    float getPrevSpeed();

    @Accessor("position")
    void setPos(float pos);

    @Accessor("speedOld")
    void setPrevSpeed(float prevSpeed);
}