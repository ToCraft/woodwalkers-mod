package tocraft.walkers.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.animal.IronGolem;

@Mixin(IronGolem.class)
public interface IronGolemEntityAccessor {
	@Accessor("remainingPersistentAngerTime")
	int getAttackTicksLeft();

	@Accessor("remainingPersistentAngerTime")
	void setAttackTicksLeft(int attackTicksLeft);
}
