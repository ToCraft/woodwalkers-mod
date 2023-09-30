package tocraft.walkers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.impl.NearbySongAccessor;
import tocraft.walkers.mixin.accessor.LivingEntityAccessor;
import tocraft.walkers.registry.WalkersEntityTags;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements NearbySongAccessor {

	@Shadow
	protected abstract int increaseAirSupply(int air);

	@Shadow
	public abstract boolean hasEffect(MobEffect effect);

	protected LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}
	
	@Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setAirSupply(I)V", ordinal = 2))
	private void cancelAirIncrement(LivingEntity livingEntity, int air) {
		// Aquatic creatures should not regenerate breath on land
		if ((Object) this instanceof Player player) {
			LivingEntity shape = PlayerShape.getCurrentShape(player);

			if (shape != null) {
				if (Walkers.isAquatic(shape)) {
					return;
				}
			}
		}

		this.setAirSupply(this.increaseAirSupply(this.getAirSupply()));
	}

	@Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z", ordinal = 0))
	private boolean slowFall(LivingEntity livingEntity, MobEffect effect) {
		if ((Object) this instanceof Player player) {
			LivingEntity shape = PlayerShape.getCurrentShape(player);

			if (shape != null) {
				if (!this.isShiftKeyDown() && shape.getType().is(WalkersEntityTags.SLOW_FALLING)) {
					return true;
				}
			}
		}

		return this.hasEffect(MobEffects.SLOW_FALLING);
	}

	@ModifyVariable(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z", ordinal = 1), ordinal = 0)
	public float applyWaterCreatureSwimSpeedBoost(float j) {
		if ((Object) this instanceof Player player) {
			LivingEntity shape = PlayerShape.getCurrentShape(player);

			// Apply 'Dolphin's Grace' status effect benefits if the player's shape is a
			// water creature
			if (shape instanceof WaterAnimal) {
				return .96f;
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
	private void returnHasNightVision(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
		if ((Object) this instanceof Player player) {
			if (effect.equals(MobEffects.NIGHT_VISION)) {
				LivingEntity shape = PlayerShape.getCurrentShape(player);

				// Apply 'Night Vision' status effect to player if they are a Bat
				if (shape instanceof Bat) {
					cir.setReturnValue(true);
				}
			}
		}
	}

	@Inject(method = "getEffect", at = @At("HEAD"), cancellable = true)
	private void returnNightVisionInstance(MobEffect effect, CallbackInfoReturnable<MobEffectInstance> cir) {
		if ((Object) this instanceof Player player) {
			if (effect.equals(MobEffects.NIGHT_VISION)) {
				LivingEntity shape = PlayerShape.getCurrentShape(player);

				// Apply 'Night Vision' status effect to player if they are a Bat
				if (shape instanceof Bat) {
					cir.setReturnValue(new MobEffectInstance(MobEffects.NIGHT_VISION, 100000, 0, false, false));
				}
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "getEyeHeight", cancellable = true)
	public void getEyeHeight(Pose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
		if ((LivingEntity) (Object) this instanceof Player player) {

			// this is cursed
			try {
				LivingEntity shape = PlayerShape.getCurrentShape(player);

				if (shape != null) {
					cir.setReturnValue(shape.getEyeHeight(pose));
				}
			} catch (Exception ignored) {
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

	@Inject(method = "canBreatheUnderwater", at = @At("HEAD"), cancellable = true)
	protected void shape_canBreatheUnderwater(CallbackInfoReturnable<Boolean> cir) {
		if ((LivingEntity) (Object) this instanceof Player player) {
			LivingEntity entity = PlayerShape.getCurrentShape(player);

			if (entity != null) {
				cir.setReturnValue(entity.canBreatheUnderwater() || entity instanceof Dolphin
						|| entity.getType().is(WalkersEntityTags.UNDROWNABLE));
			}
		}
	}

	@Unique
	private boolean nearbySongPlaying = false;

	@Environment(EnvType.CLIENT)
	@Inject(method = "setRecordPlayingNearby", at = @At("RETURN"))
	protected void shape_setRecordPlayingNearby(BlockPos songPosition, boolean playing, CallbackInfo ci) {
		if ((LivingEntity) (Object) this instanceof Player player) {
			nearbySongPlaying = playing;
		}
	}

	@Override
	public boolean shape_isNearbySongPlaying() {
		return nearbySongPlaying;
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

			if (shape != null && shape.getType().is(WalkersEntityTags.LAVA_WALKING) && state.is(FluidTags.LAVA)) {
				cir.setReturnValue(true);
			}
		}
	}

	@Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
	protected void shape_allowSpiderClimbing(CallbackInfoReturnable<Boolean> cir) {
		if ((LivingEntity) (Object) this instanceof Player player) {
			LivingEntity shape = PlayerShape.getCurrentShape(player);

			if (shape instanceof Spider) {
				cir.setReturnValue(this.horizontalCollision);
			}
		}
	}
}
