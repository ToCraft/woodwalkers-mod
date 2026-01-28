package dev.tocraft.walkers.mixin.player;

import com.mojang.authlib.GameProfile;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.api.FlightHelper;
import dev.tocraft.walkers.api.PlayerShapeChanger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"ConstantValue", "RedundantCast", "DataFlowIssue"})
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {

    public ServerPlayerEntityMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void revoke2ndShapeOnDeath(DamageSource source, CallbackInfo ci) {
        if (Walkers.CONFIG.revoke2ndShapeOnDeath && !this.isCreative() && !((ServerPlayer) (Object) this).isSpectator()) {
            PlayerShapeChanger.change2ndShape((ServerPlayer) (Object) this, null);
        }
    }

    @Inject(method = "initInventoryMenu()V", at = @At("HEAD"))
    private void onSpawn(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (Walkers.hasFlyingPermissions(player)) {
            if (!FlightHelper.hasFlight(player)) {
                FlightHelper.grantFlightTo(player);
                FlightHelper.updateFlyingSpeed(this);
                onUpdateAbilities();
            }

            FlightHelper.grantFlightTo(player);
        }
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    private void disconnectInject(CallbackInfo ci) {
        if (this.isPassenger()) {
            if (this.getVehicle() instanceof Player)
                this.stopRiding();
        }
    }
}
