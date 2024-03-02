package tocraft.walkers.mixin;

import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerShape;

@Mixin(Slime.class)
public class SlimeMixin {
    @Inject(method = "playerTouch", at = @At(value = "HEAD"), cancellable = true)
    private void onTouchPlayer(Player player, CallbackInfo ci) {
        if (PlayerShape.getCurrentShape(player) instanceof Slime) {
            ci.cancel();
        }
    }
}
