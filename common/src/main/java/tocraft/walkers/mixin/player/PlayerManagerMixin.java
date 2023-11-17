package tocraft.walkers.mixin.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerShapeChanger;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {

    @Inject(method = "respawn", at = @At(value = "RETURN"))
    private void sendResyncPacketOnRespawn(ServerPlayer player, boolean alive, CallbackInfoReturnable<ServerPlayer> cir) {
        PlayerShapeChanger.sync(player);
        PlayerShape.sync(player);
    }
}
