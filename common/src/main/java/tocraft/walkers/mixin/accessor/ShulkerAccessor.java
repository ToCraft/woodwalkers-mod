package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

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

    //#if MC<=1182
    //$$ @Invoker
    //$$ void callSetColor(DyeColor color);
    //#endif
}
