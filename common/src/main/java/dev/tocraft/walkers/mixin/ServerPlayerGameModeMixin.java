package dev.tocraft.walkers.mixin;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.api.FlightHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Shadow
    @Final
    protected ServerPlayer player;

    @Unique
    private boolean walkers$couldFly = false;

    @Inject(method = "setGameModeForPlayer", at = @At("HEAD"))
    public void onSetGameModeForPlayerHead(GameType gameModeForPlayer, GameType previousGameModeForPlayer, CallbackInfo ci) {
        walkers$couldFly = this.player.getAbilities().flying;
    }

    @Inject(method = "setGameModeForPlayer", at = @At("RETURN"))
    public void onSetGameModeForPlayerReturn(GameType gameModeForPlayer, GameType previousGameModeForPlayer, CallbackInfo ci) {
        // Use the improved hasFlyingPermissions method that checks the target gamemode
        if (gameModeForPlayer.isSurvival() && Walkers.hasFlyingPermissions(this.player, gameModeForPlayer)) {
            FlightHelper.grantFlightTo(this.player);
            this.player.getAbilities().flying = walkers$couldFly;
        } else if (gameModeForPlayer.isSurvival() && !Walkers.hasFlyingPermissions(this.player, gameModeForPlayer)) {
            // If switching to survival and player doesn't have flying permissions, revoke flight
            FlightHelper.revokeFlight(this.player);
        }
    }
}
