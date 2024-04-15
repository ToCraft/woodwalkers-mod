package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Shulker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Sheep.class)
public interface SheepAccessor {
    @Accessor
    void setEatAnimationTick(int eatAnimationTick);
}
