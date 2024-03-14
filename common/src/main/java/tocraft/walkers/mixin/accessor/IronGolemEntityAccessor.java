package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.IronGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IronGolem.class)
public interface IronGolemEntityAccessor {
    @Accessor("attackAnimationTick")
    int getAttackTicksLeft();

    @Accessor("attackAnimationTick")
    void setAttackTicksLeft(int attackTicksLeft);
}
