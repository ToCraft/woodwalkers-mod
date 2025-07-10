package tocraft.walkers.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.impl.NearbySongAccessor;
import tocraft.walkers.impl.ShapeDataProvider;
import tocraft.walkers.mixin.accessor.LivingEntityAccessor;
import tocraft.walkers.traits.ShapeTrait;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.*;

import java.util.List;

@SuppressWarnings({"resource", "unused"})
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements NearbySongAccessor {
    protected LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @WrapOperation(method = "getEffectiveGravity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 0))
    private boolean slowFall(LivingEntity instance, Holder<MobEffect> effect, Operation<Boolean> original) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                boolean bool = false;
                for (FlyingTrait<?> flyingTrait : TraitRegistry.get(shape, FlyingTrait.ID).stream().map(trait -> (FlyingTrait<?>) trait).toList()) {
                    if (flyingTrait.slowFalling) {
                        bool = true;
                        break;
                    }
                }
                if (!this.isShiftKeyDown() && (bool || TraitRegistry.has(shape, SlowFallingTrait.ID))) {
                    return true;
                }
            }
        }

        return original.call(instance, effect);
    }

    @Inject(method = "causeFallDamage", at = @At(value = "HEAD"), cancellable = true)
    private void causeFallDamage(double fallDistance, float damageMultiplier, DamageSource damageSource,
                                 CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                boolean takesFallDamage = shape.causeFallDamage(fallDistance, damageMultiplier, damageSource);
                int damageAmount = ((LivingEntityAccessor) shape).callCalculateFallDamage(fallDistance,
                        damageMultiplier);

                if (takesFallDamage && damageAmount > 0) {
                    LivingEntity.Fallsounds fallSounds = shape.getFallSounds();
                    this.playSound(damageAmount > 4 ? fallSounds.big() : fallSounds.small(), 1.0F, 1.0F);
                    ((LivingEntityAccessor) shape).callPlayBlockFallSound();
                    this.hurt(damageSources().fall(), (float) damageAmount);
                    cir.setReturnValue(true);
                } else {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "hasEffect", at = @At("HEAD"), cancellable = true)
    private void returnHasNightVision(Holder<MobEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (TraitRegistry.has(shape, MobEffectTrait.ID)) {
                List<MobEffectTrait<LivingEntity>> traitList = TraitRegistry.get(shape, MobEffectTrait.ID).stream().map(trait -> (MobEffectTrait<LivingEntity>) trait).toList();
                for (MobEffectTrait<LivingEntity> mobEffectTrait : traitList) {
                    if (!mobEffectTrait.showInInventory && mobEffectTrait.applyToSelf && effect.equals(mobEffectTrait.mobEffectInstance.getEffect())) {
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }

            for (ShapeTrait<LivingEntity> immunityTrait : TraitRegistry.get(shape, ImmunityTrait.ID)) {
                if (((ImmunityTrait<LivingEntity>) immunityTrait).effect.equals(effect.value())) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }

    @Inject(method = "getEffect", at = @At("HEAD"), cancellable = true)
    private void returnNightVisionInstance(Holder<MobEffect> effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (TraitRegistry.has(shape, MobEffectTrait.ID)) {
                List<MobEffectTrait<LivingEntity>> traitList = TraitRegistry.get(shape, MobEffectTrait.ID).stream().map(trait -> (MobEffectTrait<LivingEntity>) trait).toList();
                for (MobEffectTrait<LivingEntity> mobEffectTrait : traitList) {
                    if (!mobEffectTrait.showInInventory && mobEffectTrait.applyToSelf) {
                        MobEffectInstance mobEffectInstance = mobEffectTrait.mobEffectInstance;
                        if (effect.equals(mobEffectInstance.getEffect())) {
                            cir.setReturnValue(new MobEffectInstance(mobEffectInstance.getEffect(), mobEffectInstance.getDuration(), mobEffectInstance.getAmplifier(), mobEffectInstance.isAmbient(), mobEffectInstance.isVisible(), mobEffectInstance.showIcon()));
                            return;
                        }
                    }
                }
            }

            for (ShapeTrait<LivingEntity> immunityTrait : TraitRegistry.get(shape, ImmunityTrait.ID)) {
                if (((ImmunityTrait<LivingEntity>) immunityTrait).effect.equals(effect.value())) {
                    cir.setReturnValue(null);
                    return;
                }
            }
        }
    }


    @Inject(method = "isSensitiveToWater", at = @At("HEAD"), cancellable = true)
    protected void shape_isSensitiveToWater(CallbackInfoReturnable<Boolean> cir) {
        if ((LivingEntity) (Object) this instanceof Player player) {
            LivingEntity entity = PlayerShape.getCurrentShape(player);

            if (entity != null) {
                cir.setReturnValue(entity.isSensitiveToWater());
            }
        }
    }

    @Unique
    private boolean walkers$nearbySongPlaying = false;

    @Environment(EnvType.CLIENT)
    @Inject(method = "setRecordPlayingNearby", at = @At("RETURN"))
    protected void shape_setRecordPlayingNearby(BlockPos songPosition, boolean playing, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof Player) {
            walkers$nearbySongPlaying = playing;
        }
    }

    @Override
    public boolean shape_isNearbySongPlaying() {
        return walkers$nearbySongPlaying;
    }

    @Inject(method = "isInvertedHealAndHarm", at = @At("HEAD"), cancellable = true)
    protected void shape_isInvertedHealAndHarm(CallbackInfoReturnable<Boolean> cir) {
        if ((LivingEntity) (Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                cir.setReturnValue(shape.isInvertedHealAndHarm());
            }
        }
    }

    @Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
    protected void shape_canStandOnFluid(FluidState state, CallbackInfoReturnable<Boolean> cir) {
        if ((LivingEntity) (Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                if (shape.canStandOnFluid(state)) {
                    cir.setReturnValue(true);
                } else {
                    for (StandOnFluidTrait<?> standOnFluidTrait : TraitRegistry.get(shape, StandOnFluidTrait.ID).stream().map(entry -> (StandOnFluidTrait<?>) entry).toList()) {
                        if (state.is(standOnFluidTrait.fluidTagKey)) {
                            cir.setReturnValue(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    protected void shape_allowSpiderClimbing(CallbackInfoReturnable<Boolean> cir) {
        if ((LivingEntity) (Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                BlockState blockState = this.level().getBlockState(this.blockPosition());
                for (ClimbBlocksTrait<?> climbBlocksTrait : TraitRegistry.get(shape, ClimbBlocksTrait.ID).stream().map(entry -> (ClimbBlocksTrait<?>) entry).toList()) {
                    for (Block invalidBlock : climbBlocksTrait.invalidBlocks) {
                        if (blockState.is(invalidBlock)) {
                            return;
                        }
                    }

                    if (climbBlocksTrait.validBlocks.isEmpty()) {
                        cir.setReturnValue(this.horizontalCollision);
                    } else {
                        for (Block validBlock : climbBlocksTrait.validBlocks) {
                            if (blockState.is(validBlock)) {
                                cir.setReturnValue(!climbBlocksTrait.horizontalCollision || this.horizontalCollision);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "isSensitiveToWater", at = @At(value = "RETURN"), cancellable = true)
    private void handleWaterSensitivity(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape != null) {
                cir.setReturnValue(shape.isSensitiveToWater());
            }
        }
    }

    @Inject(method = "canFreeze", at = @At("RETURN"), cancellable = true)
    private void temperatureTraitPreventFreeze(@NotNull CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if ((Object) this instanceof Player) {
                LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);
                if (shape != null) {
                    if (TraitRegistry.has(shape, CantFreezeTrait.ID)) {
                        cir.setReturnValue(false);
                    } else {
                        for (ShapeTrait<LivingEntity> temperatureTrait : TraitRegistry.get(shape, TemperatureTrait.ID)) {
                            if (((TemperatureTrait<LivingEntity>) temperatureTrait).coldEnoughToSnow) {
                                cir.setReturnValue(false);
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void setPlayerSource(ServerLevel serverLevel, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getEntity();
        if (attacker instanceof Mob mobEntity) {
            int playerId = ((ShapeDataProvider) mobEntity).walkers$shapedPlayer();
            if (playerId != -1) {
                Entity player = serverLevel.getEntity(playerId);
                if (player instanceof Player) {
                    DamageSource damageSource = serverLevel.damageSources().playerAttack((Player) player);
                    cir.setReturnValue(this.hurtServer(serverLevel, damageSource, amount));
                }
            }
        }
    }


    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void attackForHealthTrait(ServerLevel serverLevel, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getEntity() instanceof Player player) {
            boolean didHurtTarget = cir.getReturnValue();

            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (didHurtTarget && TraitRegistry.has(shape, AttackForHealthTrait.ID)) {
                player.heal(Math.max(1, amount / 2));
            }
        }
    }
}
