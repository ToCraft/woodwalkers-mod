package dev.tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.Rabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Rabbit.class)
public interface RabbitAccessor {
    @Invoker
    void callSetVariant(Rabbit.Variant variant);
}
