package tocraft.walkers.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.impl.NearbySongAccessor;
import tocraft.walkers.impl.ShapeDataProvider;
import tocraft.walkers.mixin.accessor.LivingEntityAccessor;
import tocraft.walkers.skills.ShapeSkill;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.*;

import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements NearbySongAccessor {
    @Shadow
    public abstract void kill();

    @Shadow
    public abstract boolean hurt(DamageSource source, float amount);

    protected LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 0))
    private boolean slowFall(LivingEntity instance, Holder<MobEffect> effect, Operation<Boolean> original) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                boolean bool = false;
                for (FlyingSkill<?> flyingSkill : SkillRegistry.get(shape, FlyingSkill.ID).stream().map(skill -> (FlyingSkill<?>) skill).toList()) {
                    if (flyingSkill.slowFalling) {
                        bool = true;
                        break;
                    }
                }
                if (!this.isShiftKeyDown() && (bool || SkillRegistry.has(shape, SlowFallingSkill.ID))) {
                    return true;
                }
            }
        }

        return original.call(instance, effect);
    }

    @ModifyVariable(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 1), ordinal = 0)
    public float applyWaterCreatureSwimSpeedBoost(float j) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            // Apply 'Dolphin's Grace' status effect benefits if the player's shape is a
            // water creature
            for (ShapeSkill<LivingEntity> skill : SkillRegistry.get(shape, AquaticSkill.ID)) {
                if (((AquaticSkill<LivingEntity>) skill).isAquatic) {
                    return .96f;
                }
            }
        }

        return j;
    }

    @Inject(method = "causeFallDamage", at = @At(value = "HEAD"), cancellable = true)
    private void causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource,
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
            if (SkillRegistry.has(shape, MobEffectSkill.ID)) {
                List<MobEffectSkill<LivingEntity>> skillList = SkillRegistry.get(shape, MobEffectSkill.ID).stream().map(skill -> (MobEffectSkill<LivingEntity>) skill).toList();
                for (MobEffectSkill<LivingEntity> mobEffectSkill : skillList) {
                    if (!mobEffectSkill.showInInventory && mobEffectSkill.applyToSelf && effect.equals(mobEffectSkill.mobEffectInstance.getEffect())) {
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }
        }
    }

    @Inject(method = "getEffect", at = @At("HEAD"), cancellable = true)
    private void returnNightVisionInstance(Holder<MobEffect> effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (SkillRegistry.has(shape, MobEffectSkill.ID)) {
                List<MobEffectSkill<LivingEntity>> skillList = SkillRegistry.get(shape, MobEffectSkill.ID).stream().map(skill -> (MobEffectSkill<LivingEntity>) skill).toList();
                for (MobEffectSkill<LivingEntity> mobEffectSkill : skillList) {
                    if (!mobEffectSkill.showInInventory && mobEffectSkill.applyToSelf) {
                        MobEffectInstance mobEffectInstance = mobEffectSkill.mobEffectInstance;
                        if (effect.equals(mobEffectInstance.getEffect())) {
                            cir.setReturnValue(new MobEffectInstance(mobEffectInstance.getEffect(), mobEffectInstance.getDuration(), mobEffectInstance.getAmplifier(), mobEffectInstance.isAmbient(), mobEffectInstance.isVisible(), mobEffectInstance.showIcon()));
                            return;
                        }
                    }
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
                    for (StandOnFluidSkill<?> standOnFluidSkill : SkillRegistry.get(shape, StandOnFluidSkill.ID).stream().map(entry -> (StandOnFluidSkill<?>) entry).toList()) {
                        if (state.is(standOnFluidSkill.fluidTagKey)) {
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
                for (ClimbBlocksSkill<?> climbBlocksSkill : SkillRegistry.get(shape, ClimbBlocksSkill.ID).stream().map(entry -> (ClimbBlocksSkill<?>) entry).toList()) {
                    for (Block invalidBlock : climbBlocksSkill.invalidBlocks) {
                        if (blockState.is(invalidBlock)) {
                            return;
                        }
                    }

                    if (climbBlocksSkill.validBlocks.isEmpty()) {
                        cir.setReturnValue(this.horizontalCollision);
                    } else {
                        for (Block validBlock : climbBlocksSkill.validBlocks) {
                            if (blockState.is(validBlock)) {
                                cir.setReturnValue(!climbBlocksSkill.horizontalCollision || this.horizontalCollision);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }


    @Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/food/FoodProperties;)V"))
    private void regenerateWoolFromFood(Level level, ItemStack food, CallbackInfoReturnable<ItemStack> cir) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape instanceof Sheep sheepShape) {
                if (sheepShape.isSheared())
                    sheepShape.setSheared(false);
            }
        }
    }

    @Inject(method = "eat", at = @At(value = "RETURN"))
    private void dieFromCookies(Level level, ItemStack food, CallbackInfoReturnable<ItemStack> cir) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape instanceof Parrot) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 900));
                if (player.isCreative() || !this.isInvulnerable()) {
                    this.hurt(this.damageSources().playerAttack(player), Float.MAX_VALUE);
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
    private void temperatureSkillPreventFreeze(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if ((Object) this instanceof Player) {
                LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);
                if (shape != null) {
                    for (ShapeSkill<LivingEntity> temperatureSkill : SkillRegistry.get(shape, TemperatureSkill.ID)) {
                        if (((TemperatureSkill<LivingEntity>) temperatureSkill).coldEnoughToSnow) {
                            cir.setReturnValue(false);
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void setPlayerSource(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = source.getEntity();
        if (attacker instanceof Mob mobEntity) {
            if (((ShapeDataProvider) mobEntity).walkers$isShape()) {
                DamageSource playerDamageSource = ((ShapeDataProvider) mobEntity).walkers$playerDamageSource();
                if (playerDamageSource != null) {
                    cir.setReturnValue(this.hurt(playerDamageSource, amount));
                }
            }
        }
    }


    @Inject(method = "hurt", at = @At("RETURN"))
    private void attackForHealthSkill(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getEntity() instanceof Player player) {
            boolean didHurtTarget = cir.getReturnValue();

            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (didHurtTarget && SkillRegistry.has(shape, AttackForHealthSkill.ID)) {
                player.heal(Math.max(1, amount / 2));
            }
        }
    }
}
