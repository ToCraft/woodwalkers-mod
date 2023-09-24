package tocraft.walkers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.WalkAnimationState;

@Mixin(WalkAnimationState.class)
public interface LimbAnimatorAccessor {

	@Accessor("speedOld")
	float getPrevSpeed();

	@Accessor("speedOld")
	void setPrevSpeed(float prevSpeed);

	@Accessor("speed")
	float getSpeed();

	@Accessor("speed")
	void setSpeed(float speed);

	@Accessor("position")
	float getPos();

	@Accessor("position")
	void setPos(float pos);
}