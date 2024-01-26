package tocraft.walkers.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerShape;

@SuppressWarnings("ConstantConditions")
@Mixin(Player.class)
public class PlayerByteStatusMixin {

    // When a player receives a handleStatus byte, pass it on to their shape.
    @Inject(method = "handleEntityEvent", at = @At("RETURN"))
    private void shape$passByteStatus(byte status, CallbackInfo ci) {
        @Nullable LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);
        if (shape != null) {
            shape.handleEntityEvent(status);
        }
    }
}
