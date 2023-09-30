package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.monster.Ravager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ravager.class)
public interface RavagerEntityAccessor {
    @Accessor
    int getAttackTick();

    @Accessor
    void setAttackTick(int attackTick);
}
