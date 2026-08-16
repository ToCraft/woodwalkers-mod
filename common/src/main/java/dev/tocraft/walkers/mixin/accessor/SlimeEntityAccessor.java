package dev.tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractCubeMob.class)
public interface SlimeEntityAccessor {

    @Invoker
    void callSetSize(int size, boolean heal);
}
