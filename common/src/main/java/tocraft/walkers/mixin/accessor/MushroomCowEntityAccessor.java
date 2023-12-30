package tocraft.walkers.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.MushroomCow.MushroomType;

@Mixin(MushroomCow.class)
public interface MushroomCowEntityAccessor {
	@Invoker("setMushroomType")
	void setMushroomType(MushroomType type);
}
