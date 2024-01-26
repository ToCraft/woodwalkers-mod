package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.IronGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IronGolem.class)
public interface IronGolemEntityAccessor {
    @Accessor("remainingPersistentAngerTime")
    int getAttackTicksLeft();

    @Accessor("remainingPersistentAngerTime")
    void setAttackTicksLeft(int attackTicksLeft);
}
