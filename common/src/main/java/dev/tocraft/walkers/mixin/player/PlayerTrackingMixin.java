package dev.tocraft.walkers.mixin.player;

import dev.tocraft.walkers.api.PlayerShape;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntity.class)
public class PlayerTrackingMixin {

    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "addPairing", at = @At("RETURN"))
    private void sendPairingWalkersPackets(ServerPlayer newlyTracked, CallbackInfo ci) {
        if (this.entity instanceof ServerPlayer player) {
            PlayerShape.sync(newlyTracked, player);
            PlayerShape.sync(player, newlyTracked);
        }
    }
}
