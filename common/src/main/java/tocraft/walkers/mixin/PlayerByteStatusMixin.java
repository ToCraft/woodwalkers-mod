package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerShape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Mixin(PlayerEntity.class)
public class PlayerByteStatusMixin {

    // When a player receives a handleStatus byte, pass it on to their shape.
    @Inject(method = "handleStatus", at = @At("RETURN"))
    private void shape$passByteStatus(byte status, CallbackInfo ci) {
        @Nullable LivingEntity shape = PlayerShape.getCurrentShape((PlayerEntity) (Object) this);
        if(shape != null) {
            shape.handleStatus(status);
        }
    }
}
