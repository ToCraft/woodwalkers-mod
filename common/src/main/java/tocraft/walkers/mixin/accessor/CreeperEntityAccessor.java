package tocraft.walkers.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.monster.Creeper;

@Mixin(Creeper.class)
public interface CreeperEntityAccessor {
	@Accessor
	void callSwell(int currentFuseTime);
}
