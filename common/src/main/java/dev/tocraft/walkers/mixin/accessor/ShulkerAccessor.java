package dev.tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(Shulker.class)
public interface ShulkerAccessor {

    @Invoker
    boolean callUpdatePeekAmount();

    @Invoker
    void callOnPeekAmountChange();

    @Invoker
    int callGetRawPeekAmount();

    @Invoker
    void callSetRawPeekAmount(int peekAmount);

    @Invoker
    void callSetVariant(Optional<DyeColor> variant);
}
