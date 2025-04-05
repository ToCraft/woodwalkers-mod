package tocraft.walkers.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.animal.sheep.Sheep;

@Mixin(Sheep.class)
public interface SheepAccessor {
    @Accessor
    void setEatAnimationTick(int eatAnimationTick);
}
