package dev.tocraft.walkers.mixin;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.api.FlightHelper;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.traits.TraitRegistry;
import dev.tocraft.walkers.traits.impl.FlyingTrait;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.FlyingAnimal;
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
        // Handle flight permissions based on the NEW gamemode, not the old one
        if (gameModeForPlayer.isSurvival()) {
            // Check if player has flying permissions based on their current shape (ignoring creative mode)
            LivingEntity shape = PlayerShape.getCurrentShape(this.player);
            boolean hasShapeFlight = shape != null && Walkers.CONFIG.enableFlight
                    && (TraitRegistry.has(shape, FlyingTrait.ID) || shape instanceof FlyingAnimal);
            
            if (hasShapeFlight) {
                // Player has a flying shape, grant flight and restore flying state
                FlightHelper.grantFlightTo(this.player);
                this.player.getAbilities().flying = walkers$couldFly;
            } else {
                // Player doesn't have a flying shape, revoke flight
                FlightHelper.revokeFlight(this.player);
            }
        }
    }
}
