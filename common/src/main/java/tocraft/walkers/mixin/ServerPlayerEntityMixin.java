package tocraft.walkers.mixin;

import com.mojang.authlib.GameProfile;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.FlightHelper;
import tocraft.walkers.api.platform.WalkersConfig;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Shadow public abstract boolean isCreative();
    @Shadow public abstract boolean isSpectator();
    @Shadow public abstract void sendMessage(Text message, boolean actionBar);

        public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(
            method = "onDeath",
            at = @At("HEAD")
    )
    private void revoke2ndShapeOnDeath(DamageSource source, CallbackInfo ci) {
        if(WalkersConfig.getInstance().revoke2ndShapeOnDeath() && !this.isCreative() && !this.isSpectator()) {
            PlayerShapeChanger.change2ndShape((ServerPlayerEntity) (Object) this, null);
        }
    }

    @Inject(
            method = "onSpawn()V",
            at = @At("HEAD")
    )
    private void onSpawn(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if(Walkers.hasFlyingPermissions(player)) {
            if(!FlightHelper.hasFlight(player)) {
                FlightHelper.grantFlightTo(player);
                getAbilities().setFlySpeed(WalkersConfig.getInstance().flySpeed());
                sendAbilitiesUpdate();
            }

            FlightHelper.grantFlightTo(player);
        }
    }
}
