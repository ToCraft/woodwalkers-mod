package tocraft.walkers.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
//#if MC>=1205
import net.minecraft.core.Holder;
import net.minecraft.world.food.FoodProperties;
//#endif
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
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
import tocraft.craftedcore.patched.CEntity;
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
    @Shadow
    public abstract void kill();

    @Shadow
    public abstract boolean hurt(DamageSource source, float amount);

    protected LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    //#if MC>=1205
    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 0))
    private boolean slowFall(LivingEntity instance, Holder<MobEffect> effect, Operation<Boolean> original) {
    //#else
    //$$ @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z", ordinal = 0))
    //$$ private boolean slowFall(LivingEntity instance, MobEffect effect, Operation<Boolean> original) {
        //#endif
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

    //#if MC>=1205
    @ModifyVariable(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 1), ordinal = 0)
    //#else
    //$$ @ModifyVariable(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z", ordinal = 1), ordinal = 0)
    //#endif
    public float applyWaterCreatureSwimSpeedBoost(float j) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            // Apply 'Dolphin's Grace' status effect benefits if the player's shape is a
            // water creature
            for (ShapeTrait<LivingEntity> trait : TraitRegistry.get(shape, AquaticTrait.ID)) {
                if (((AquaticTrait<LivingEntity>) trait).isAquatic) {
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
                    //#if MC>1182
                    this.hurt(damageSources().fall(), (float) damageAmount);
                    //#else
                    //$$ this.hurt(DamageSource.FALL, (float) damageAmount);
                    //#endif
                    cir.setReturnValue(true);
                } else {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "hasEffect", at = @At("HEAD"), cancellable = true)
    //#if MC>=1205
    private void returnHasNightVision(Holder<MobEffect> effect, CallbackInfoReturnable<Boolean> cir) {
    //#else
    //$$ private void returnHasNightVision(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
        //#endif
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
        }
    }

    @Inject(method = "getEffect", at = @At("HEAD"), cancellable = true)
    //#if MC>=1205
    private void returnNightVisionInstance(Holder<MobEffect> effect, CallbackInfoReturnable<MobEffectInstance> cir) {
    //#else
    //$$ private void returnNightVisionInstance(MobEffect effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        //#endif
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
        }
    }

    //#if MC<1205
    //$$ @Inject(at = @At("HEAD"), method = "getEyeHeight", cancellable = true)
    //$$ public void getEyeHeight(Pose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
    //$$     if ((LivingEntity) (Object) this instanceof Player player) {
    //$$
    //$$         // this is cursed
    //$$         try {
    //$$             LivingEntity shape = PlayerShape.getCurrentShape(player);
    //$$
    //$$             if (shape != null) {
    //$$                 float shapeEyeHeight = shape.getEyeHeight(pose);
    //$$                 if (pose == Pose.CROUCHING && TraitRegistry.has(shape, HumanoidTrait.ID)) {
    //$$                     cir.setReturnValue(shapeEyeHeight * 1.27F / 1.62F);
    //$$                     return;
    //$$                 }
    //$$
    //$$                 cir.setReturnValue(shapeEyeHeight);
    //$$             }
    //$$         } catch (Exception ignored) {
    //$$         }
    //$$     }
    //$$ }
    //#endif

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
                BlockState blockState = CEntity.level(this).getBlockState(this.blockPosition());
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


    //#if MC>1206
    //$$ @Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/food/FoodProperties;)V"))
    //$$ private void regenerateWoolFromFood(Level level, ItemStack food, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> cir) {
    //#else
    //#if MC==1206
    @Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/food/FoodProperties;)V"))
    //#else
    //$$ @Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V"))
    //#endif
    private void regenerateWoolFromFood(Level level, ItemStack food, CallbackInfoReturnable<ItemStack> cir) {
        //#endif
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape instanceof Sheep sheepShape) {
                if (sheepShape.isSheared())
                    sheepShape.setSheared(false);
            }
        }
    }

    //#if MC>1206
    //$$ @Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "RETURN"))
    //$$ private void dieFromCookies(Level level, ItemStack food, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> cir) {
    //#else
    @Inject(method = "eat", at = @At(value = "RETURN"))
    private void dieFromCookies(Level level, ItemStack food, CallbackInfoReturnable<ItemStack> cir) {
        //#endif
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape instanceof Parrot) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 900));
                if (player.isCreative() || !this.isInvulnerable()) {
                    //#if MC>1182
                    this.hurt(this.damageSources().playerAttack(player), Float.MAX_VALUE);
                    //#else
                    //$$ this.hurt(DamageSource.playerAttack(player), Float.MAX_VALUE);
                    //#endif
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
    private void temperatureTraitPreventFreeze(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if ((Object) this instanceof Player) {
                LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);
                if (shape != null) {
                    for (ShapeTrait<LivingEntity> temperatureTrait : TraitRegistry.get(shape, TemperatureTrait.ID)) {
                        if (((TemperatureTrait<LivingEntity>) temperatureTrait).coldEnoughToSnow) {
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
    private void attackForHealthTrait(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getEntity() instanceof Player player) {
            boolean didHurtTarget = cir.getReturnValue();

            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (didHurtTarget && TraitRegistry.has(shape, AttackForHealthTrait.ID)) {
                player.heal(Math.max(1, amount / 2));
            }
        }
    }
}
