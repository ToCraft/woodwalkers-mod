package dev.tocraft.walkers.mixin.client;

import com.mojang.authlib.GameProfile;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.api.model.EntityUpdater;
import dev.tocraft.walkers.api.model.EntityUpdaters;
import dev.tocraft.walkers.mixin.accessor.LivingEntityAccessor;
import dev.tocraft.walkers.traits.TraitRegistry;
import dev.tocraft.walkers.traits.impl.FearedTrait;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@SuppressWarnings("DataFlowIssue")
@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {
    public LocalPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }
    @Unique
    private final Random walkers$random = new Random();
    @Unique private int walkers$shapeSoundCooldown = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void intenseShakeNearOcelot(CallbackInfo ci) {
        if (walkers$shapeSoundCooldown > 0) {
            walkers$shapeSoundCooldown--;
        }

        LocalPlayer player = (LocalPlayer) (Object) this;
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (shape != null) {
            double maxRadius = 16.0;
            AABB area = player.getBoundingBox().inflate(maxRadius);
            List<LivingEntity> feared = player.level().getEntitiesOfClass(LivingEntity.class, area, entity -> {
                for (FearedTrait<?> trait : TraitRegistry.get(entity, FearedTrait.ID).stream().map(entry -> (FearedTrait<?>) entry).toList()) {
                    if (trait.isFearful(shape)) {
                        return true;
                    }
                }
                return false;
            });

            if (!feared.isEmpty()) {
                double closestDist = maxRadius;
                for (LivingEntity fear : feared) {
                    double dist = player.distanceTo(fear);
                    if (dist < closestDist) {
                        closestDist = dist;
                    }
                }

                // Linear proximity factor (1.0 at 0 blocks, 0.0 at maxRadius)
                double proximity = (maxRadius - closestDist) / maxRadius;
                float intensity = (float) Math.pow(proximity, 2.5);
                float maxShake = 8.0F * intensity;

                if (maxShake > 0.1F) {
                    float shakeX = (walkers$random.nextFloat() - 0.5F) * maxShake;
                    float shakeY = (walkers$random.nextFloat() - 0.5F) * maxShake;
                    player.setXRot(player.getXRot() + shakeX);
                    player.setYRot(player.getYRot() + shakeY);
                    player.xRotO += shakeX;
                    player.yRotO += shakeY;
                }

                if (walkers$shapeSoundCooldown == 0 && intensity > 0.15F) {
                    float volume = intensity * 1.5F;
                    float pitch = 0.4F + (walkers$random.nextFloat() * 0.2F);

                    player.level().playSound(
                            player,
                            player.getX(), player.getY(), player.getZ(),
                            SoundEvents.BREEZE_IDLE_GROUND, // creepy wind sound
                            SoundSource.PLAYERS,
                            volume,
                            pitch
                    );
                    walkers$shapeSoundCooldown = Math.max(20, (int) (60 * (1.0F - intensity)));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "aiStep", at = @At("RETURN"))
    private void fixEntityAnimations(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Player) this;
        LivingEntity shape = PlayerShape.getCurrentShape(player);
        if (shape != null) {
            EntityUpdater<LivingEntity> entityUpdater = EntityUpdaters.getUpdater((EntityType<@NotNull LivingEntity>) shape.getType());
            if (entityUpdater != null) {
                entityUpdater.update(player, shape);
            }
        }
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
            }
        }
    }

    @Override
    protected float getJumpPower() {
        return PlayerShape.getCurrentShape(this) instanceof AbstractHorse ? 0 : super.getJumpPower(); // don't jump while being a horse
    }
}
