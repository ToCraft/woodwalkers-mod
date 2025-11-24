package dev.tocraft.walkers.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tocraft.walkers.impl.ShapeDataProvider;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixin {
    @Shadow
    public abstract void standIfPossible();

    @Inject(method = "canJump", at = @At("HEAD"), cancellable = true)
    private void shape_forceCanJump(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ShapeDataProvider shape && shape.walkers$shapedPlayer() != -1) {
            cir.setReturnValue(true); // force canJump
        }
    }

    @WrapOperation(method = "onPlayerJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;isSaddled()Z"))
    private boolean shape_forceJump(AbstractHorse instance, Operation<Boolean> original) {
        boolean org = original.call(instance);
        if (!org && (Object) this instanceof ShapeDataProvider shape && shape.walkers$shapedPlayer() != -1) {
            this.standIfPossible();
            return true; // force onPlayerJump to work on shapes
        }
        return org;
    }
}
