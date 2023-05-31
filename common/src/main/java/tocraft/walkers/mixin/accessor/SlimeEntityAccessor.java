package tocraft.walkers.mixin.accessor;

import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SlimeEntity.class)
public interface SlimeEntityAccessor {

    @Invoker
    void callSetSize(int size, boolean heal);
}
