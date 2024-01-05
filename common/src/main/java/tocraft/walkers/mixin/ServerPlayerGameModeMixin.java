package tocraft.walkers.mixin;

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
import tocraft.walkers.Walkers;
import tocraft.walkers.api.FlightHelper;

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
        if (gameModeForPlayer.isSurvival() && Walkers.hasFlyingPermissions(this.player)) {
            FlightHelper.grantFlightTo(this.player);
            this.player.getAbilities().flying = walkers$couldFly;
        }
    }
}
