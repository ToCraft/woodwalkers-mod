package dev.tocraft.walkers.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.WalkersClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
@Environment(EnvType.CLIENT)
public class MouseHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ScrollWheelHandler;getNextScrollWheelSelection(DII)I"))
    private int handleScrollInVariantsMenu(double direction, int current, int size, Operation<Integer> original) {
        if (!minecraft.options.hideGui && WalkersClient.isRenderingVariantsMenu && Walkers.CONFIG.unlockEveryVariant && minecraft.screen == null) {
            WalkersClient.variantOffset -= (int) direction;
            return current;
        } else {
            return original.call(direction, current, size);
        }
    }
}
