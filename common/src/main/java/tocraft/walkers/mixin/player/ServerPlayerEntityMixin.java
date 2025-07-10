package tocraft.walkers.mixin.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.FlightHelper;
import tocraft.walkers.api.PlayerShapeChanger;

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
}
