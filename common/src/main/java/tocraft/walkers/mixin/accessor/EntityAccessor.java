package tocraft.walkers.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public interface EntityAccessor {
	@Accessor("wasTouchingWater")
	void setTouchingWater(boolean touchingWater);

	@Accessor
	void setVehicle(Entity vehicle);

	@Invoker("setSharedFlag")
	void shape_callSetFlag(int index, boolean value);
}
