package dev.tocraft.walkers.mixin.player;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.mixin.LivingEntityMixin;
import dev.tocraft.walkers.mixin.accessor.EntityAccessor;
import dev.tocraft.walkers.mixin.accessor.IronGolemEntityAccessor;
import dev.tocraft.walkers.mixin.accessor.LivingEntityAccessor;
import dev.tocraft.walkers.mixin.accessor.RavagerEntityAccessor;
import dev.tocraft.walkers.traits.ShapeTrait;
import dev.tocraft.walkers.traits.TraitRegistry;
import dev.tocraft.walkers.traits.impl.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@SuppressWarnings({"ConstantConditions", "RedundantCast"})
@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {
    private PlayerEntityMixin(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @Inject(method = "getDefaultDimensions", at = @At("HEAD"), cancellable = true)
    private void getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        LivingEntity entity = PlayerShape.getCurrentShape((Player) (Object) this);

        if (entity != null) {
            if (pose != Pose.CROUCHING || !TraitRegistry.has(entity, HumanoidTrait.ID)) {
                EntityDimensions shapeDimensions = entity.getDimensions(pose);
                cir.setReturnValue(shapeDimensions);
            }
        }
    }

    @Inject(method = "attack", at = @At("HEAD"))
    protected void shape_tryAttack(Entity target, CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (shape instanceof IronGolem golem) {
            ((IronGolemEntityAccessor) golem).setAttackTicksLeft(10);
        } else if (shape instanceof Warden warden) {
            warden.attackAnimationState.start(tickCount);
        } else if (shape instanceof Ravager ravager) {
            ((RavagerEntityAccessor) ravager).setAttackTick(10);
        } else if (shape instanceof WitherSkeleton && target instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(MobEffects.WITHER, 200), this);
        } else if (shape instanceof Bee bee && bee.isAngry() && target instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(MobEffects.POISON, 200), this);
        } else if (shape instanceof Pufferfish pufferfish && !level().isClientSide) {
            int i = pufferfish.getPuffState();

            if (target instanceof LivingEntity livingTarget) {
                if (livingTarget.hurtServer((ServerLevel) level(), this.damageSources().mobAttack((Player) (Object) this), (float) (1 + i))) {
                    livingTarget.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * i, 0), (Player) (Object) this);
                    this.playSound(SoundEvents.PUFFER_FISH_STING, 1.0F, 1.0F);

                    if (livingTarget instanceof ServerPlayer serverPlayerTarget && !this.isSilent()) {
                        serverPlayerTarget.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.PUFFER_FISH_STING, 0.0F));
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickGolemAttackTicks(CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (shape instanceof IronGolem golem) {
            IronGolemEntityAccessor accessor = (IronGolemEntityAccessor) golem;
            if (accessor.getAttackTicksLeft() > 0) {
                accessor.setAttackTicksLeft(accessor.getAttackTicksLeft() - 1);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickRavagerAttackTicks(CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (shape instanceof Ravager ravager) {
            RavagerEntityAccessor accessor = (RavagerEntityAccessor) ravager;
            if (accessor.getAttackTick() > 0) {
                accessor.setAttackTick(accessor.getAttackTick() - 1);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickWardenSneakingAnimation(CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (shape instanceof Warden warden) {
            if (isShiftKeyDown()) {
                if (!warden.sniffAnimationState.isStarted()) {
                    warden.sniffAnimationState.start(tickCount);
                }
            } else {
                warden.sniffAnimationState.stop();
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickFire(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (!player.level().isClientSide && !player.isCreative() && !player.isSpectator()) {
            // check if the player is shape
            if (shape != null) {
                // check if the player's current shape burns in sunlight
                if (TraitRegistry.has(shape, BurnInDaylightTrait.ID)) {
                    boolean bl = this.walkers$isInDaylight();
                    // handle night burning
                    for (BurnInDaylightTrait<?> trait : TraitRegistry.get(shape, BurnInDaylightTrait.ID).stream().map(trait -> ((BurnInDaylightTrait<?>) trait)).toList()) {
                        bl = (bl && !trait.burnInMoonlightInstead) || (!this.walkers$isInDaylight() && trait.burnInMoonlightInstead);
                    }
                    if (bl) {

                        // Can't burn in the rain
                        if (player.level().isRaining()) {
                            return;
                        }

                        // check for helmets to negate burning
                        ItemStack itemStack = player.getItemBySlot(EquipmentSlot.HEAD);
                        if (!itemStack.isEmpty()) {
                            if (itemStack.isDamageableItem()) {

                                // damage stack instead of burning player
                                itemStack.setDamageValue(itemStack.getDamageValue() + player.getRandom().nextInt(2));
                                if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
                                    player.onEquippedItemBroken(itemStack.getItem(), EquipmentSlot.HEAD);
                                    player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                                }
                            }

                            bl = false;
                        }

                        // set player on fire
                        if (bl) {
                            player.igniteForSeconds(8);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Unique
    private boolean walkers$isInDaylight() {
        if (!this.level().isClientSide && !this.level().dimensionType().hasFixedTime() && this.level().getSkyDarken() < 4) {
            float brightnessAtEyes = getLightLevelDependentMagicValue();
            BlockPos daylightTestPosition = BlockPos.containing(getX(), (double) Math.round(getY()), getZ());

            // move test position up one block for boats
            if (getVehicle() instanceof Boat) {
                daylightTestPosition = daylightTestPosition.above();
            }

            return brightnessAtEyes > 0.5F && random.nextFloat() * 30.0F < (brightnessAtEyes - 0.4F) * 2.0F && this.level().canSeeSky(daylightTestPosition);
        }

        return false;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickTemperature(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        LivingEntity shape = PlayerShape.getCurrentShape(player);


        if (!player.isCreative() && !player.isSpectator()) {
            // check if the player is morphed
            if (shape != null) {
                // damage player if they are a shape that gets hurt by low or high temperatures
                final boolean couldEnoughToSnow = this.level().getBiome(blockPosition()).value().coldEnoughToSnow(blockPosition(), this.level().getSeaLevel());
                for (TemperatureTrait<?> temperaturetrait : TraitRegistry.get(shape, TemperatureTrait.ID).stream().map(entry -> (TemperatureTrait<?>) entry).toList()) {
                    if (!temperaturetrait.coldEnoughToSnow == couldEnoughToSnow) {
                        player.hurt(this.level().damageSources().onFire(), 1.0f);
                        break;
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickWalkers(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        // assign basic data to entity from player on server; most data transferring
        // occurs on client
        if (shape != null) {
            shape.setShiftKeyDown(player.isShiftKeyDown());
            shape.setPose(player.getPose());
            shape.setSwimming(player.isSwimming());

            if (!this.level().isClientSide) {
                shape.setPosRaw(player.getX(), player.getY(), player.getZ());
                shape.setYHeadRot(player.getYHeadRot());
                shape.setJumping(((LivingEntityAccessor) player).isJumping());
                shape.setSprinting(player.isSprinting());
                shape.setArrowCount(player.getArrowCount());
                shape.setInvulnerable(true);
                shape.setNoGravity(true);
                shape.setSwimming(player.isSwimming());
                shape.startUsingItem(player.getUsedItemHand());

                if (shape instanceof TamableAnimal) {
                    ((TamableAnimal) shape).setInSittingPose(player.isShiftKeyDown());
                    ((TamableAnimal) shape).setOrderedToSit(player.isShiftKeyDown());
                } else if (shape instanceof Fox) {
                    ((Fox) shape).setSitting(player.isShiftKeyDown());
                    ((Fox) shape).setJumping(!player.onGround());
                }

                ((EntityAccessor) shape).shape_callSetFlag(7, player.isFallFlying());

                ((LivingEntityAccessor) shape).callUpdatingUsingItem();
                PlayerShape.sync((ServerPlayer) player); // safe cast - context is server world
            }
        }
    }

    @Inject(method = "makeStuckInBlock", at = @At("HEAD"), cancellable = true)
    private void onStuckInBlock(BlockState state, Vec3 motionMultiplier, CallbackInfo ci) {
        if (PlayerShape.getCurrentShape((Player) (Object) this) instanceof Spider && state.is(Blocks.COBWEB)) {
            ci.cancel();
        }
    }

    @Inject(method = "touch", at = @At("HEAD"))
    private void onTouch(Entity entity, CallbackInfo ci) {
        Player ownPlayer = (Player) (Object) this;
        if (!this.level().isClientSide && ownPlayer.isAlive() && PlayerShape.getCurrentShape(ownPlayer) instanceof Slime slimeShape && (entity instanceof Player targetPlayer && !(PlayerShape.getCurrentShape(targetPlayer) instanceof Slime))) {
            int i = slimeShape.getSize();
            boolean wasHurt = targetPlayer.hurtServer((ServerLevel) level(), ownPlayer.damageSources().mobAttack(ownPlayer), (float) ownPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE));
            if (this.distanceToSqr(targetPlayer) < 0.6 * (double) i * 0.6 * (double) i && ownPlayer.hasLineOfSight(targetPlayer) && wasHurt) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                EnchantmentHelper.doPostAttackEffects((ServerLevel) ownPlayer.level(), targetPlayer, ownPlayer.damageSources().mobAttack(ownPlayer));
            }
        }
    }

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void handeReinforcementsTrait(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        LivingEntity shape = PlayerShape.getCurrentShape(player);
        if (source.getEntity() instanceof LivingEntity livingAttacker && shape != null) {
            for (ReinforcementsTrait<LivingEntity> reinforcementTrait : TraitRegistry.get(shape, ReinforcementsTrait.ID).stream().map(trait -> (ReinforcementsTrait<LivingEntity>) trait).toList()) {
                double d = reinforcementTrait.getRange();
                AABB aABB = AABB.unitCubeFromLowerCorner(this.position()).inflate(d, 10.0, d);
                Iterator<? extends LivingEntity> var5 = level.getEntitiesOfClass(Mob.class, aABB, EntitySelector.NO_SPECTATORS.and(entity -> {
                    if (reinforcementTrait.hasReinforcements()) {
                        return reinforcementTrait.isReinforcement(entity);
                    } else {
                        return shape.getClass().isInstance(entity);
                    }
                })).iterator();

                while (true) {
                    Mob mob;
                    while (true) {
                        if (!var5.hasNext()) {
                            return;
                        }

                        mob = (Mob) var5.next();
                        if (shape != mob && mob.getTarget() == null) {

                            boolean bl = false;

                            if (!bl) {
                                break;
                            }
                        }
                    }

                    mob.setTarget(livingAttacker);
                }
            }
        }
    }

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void instantDieOnDamageTypeTrait(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);
        if (shape != null) {
            for (ShapeTrait<LivingEntity> instantDieOnDamageTypetrait : TraitRegistry.get(shape, InstantDieOnDamageMsgTrait.ID)) {
                if (source.getMsgId().equals(((InstantDieOnDamageMsgTrait<LivingEntity>) instantDieOnDamageTypetrait).msgId)) {
                    this.kill(level);
                }
            }
        }
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void invulnerabilityTrait(ServerLevel level, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);
        if (!damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && TraitRegistry.has(shape, InvulnerabilityTrait.ID)) {
            cir.setReturnValue(true);
        }
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;tick(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private boolean preventFoodDataTick(FoodData instance, ServerPlayer player) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);
        return player.hasEffect(MobEffects.SATURATION) || !TraitRegistry.has(shape, AttackForHealthTrait.ID);
    }

    @Inject(method = "canEat", at = @At("RETURN"), cancellable = true)
    private void onCanEat(boolean canAlwaysEat, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (TraitRegistry.has(PlayerShape.getCurrentShape((Player) (Object) this), AttackForHealthTrait.ID)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "causeFoodExhaustion", at = @At("HEAD"), cancellable = true)
    private void onExhaustion(float exhaustion, CallbackInfo ci) {
        if (TraitRegistry.has(PlayerShape.getCurrentShape((Player) (Object) this), AttackForHealthTrait.ID)) {
            ci.cancel();
        }
    }
}
