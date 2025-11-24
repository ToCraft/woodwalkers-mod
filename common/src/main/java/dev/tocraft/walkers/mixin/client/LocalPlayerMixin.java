package dev.tocraft.walkers.mixin.client;

import com.mojang.authlib.GameProfile;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.mixin.accessor.LivingEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("DataFlowIssue")
@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {
    public LocalPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "jumpableVehicle", at = @At("RETURN"), cancellable = true)
    private void shape_jumpable(@NotNull CallbackInfoReturnable<PlayerRideableJumping> cir) {
        PlayerRideableJumping r = cir.getReturnValue();
        if (r == null) {
            LivingEntity shape = PlayerShape.getCurrentShape((Player) this);
            if (shape instanceof AbstractHorse jump) {
                cir.setReturnValue(jump);
            }
        }
    }

    // FIXME: Test from server-side, fix animations!
    @Inject(method = "sendRidingJump", at = @At("HEAD"))
    private void shape_doJump(CallbackInfo ci) { // jump
        if (this.onGround()) {
            LivingEntity shape = PlayerShape.getCurrentShape(this);
            if (shape instanceof AbstractHorse) {
                float playerJumpPendingScale = 0.4F + 0.4F * Mth.floor(((LocalPlayer) (Object) this).getJumpRidingScale() * 100.0F) / 90F;
                double d = ((LivingEntityAccessor) shape).callGetJumpPower(playerJumpPendingScale);
                Vec3 vec3 = this.getDeltaMovement();
                this.setDeltaMovement(vec3.x, d, vec3.z);
                this.hasImpulse = true;
            }
        }
    }

    @Override
    protected float getJumpPower() {
        return PlayerShape.getCurrentShape(this) instanceof AbstractHorse ? 0 : super.getJumpPower(); // don't jump while being a horse
    }
}
