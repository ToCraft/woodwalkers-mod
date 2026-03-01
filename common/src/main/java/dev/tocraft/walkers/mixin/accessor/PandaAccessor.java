package dev.tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.Panda;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Panda.class)
public interface PandaAccessor {

    @Invoker
    void callUpdateSitAmount();
}
