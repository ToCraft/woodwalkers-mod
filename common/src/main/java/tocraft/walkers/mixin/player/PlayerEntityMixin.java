package tocraft.walkers.mixin.player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
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
//#if MC>1182
import net.minecraft.world.entity.monster.warden.Warden;
//#endif
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.craftedcore.patched.CEntity;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.mixin.LivingEntityMixin;
import tocraft.walkers.mixin.accessor.EntityAccessor;
import tocraft.walkers.mixin.accessor.IronGolemEntityAccessor;
import tocraft.walkers.mixin.accessor.LivingEntityAccessor;
import tocraft.walkers.mixin.accessor.RavagerEntityAccessor;
import tocraft.walkers.traits.ShapeTrait;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.*;

import java.util.Iterator;

@SuppressWarnings({"ConstantConditions", "RedundantCast"})
@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {
    private PlayerEntityMixin(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    //#if MC>=1205
    @Inject(method = "getDefaultDimensions", at = @At("HEAD"), cancellable = true)
    private void getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        LivingEntity entity = PlayerShape.getCurrentShape((Player) (Object) this);
    
        if (entity != null) {
            EntityDimensions shapeDimensions = entity.getDimensions(pose);
            if (pose == Pose.CROUCHING && TraitRegistry.has(entity, HumanoidTrait.ID)) {
                cir.setReturnValue(EntityDimensions.scalable(shapeDimensions.width(), shapeDimensions.height() * 1.5F / 1.8F));
            } else {
                cir.setReturnValue(shapeDimensions);
            }
        }
    }
    //#else
    //$$ @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    //$$ private void getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
    //$$     LivingEntity entity = PlayerShape.getCurrentShape((Player) (Object) this);
    //$$
    //$$     if (entity != null) {
    //$$         EntityDimensions shapeDimensions = entity.getDimensions(pose);
    //$$         if (pose == Pose.CROUCHING && TraitRegistry.has(entity, HumanoidTrait.ID)) {
    //$$             cir.setReturnValue(EntityDimensions.scalable(shapeDimensions.width, shapeDimensions.height * 1.5F / 1.8F));
    //$$         } else {
    //$$             cir.setReturnValue(shapeDimensions);
    //$$         }
    //$$     }
    //$$ }
    //#endif

    @Inject(method = "attack", at = @At("HEAD"))
    protected void shape_tryAttack(Entity target, CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (shape instanceof IronGolem golem) {
            ((IronGolemEntityAccessor) golem).setAttackTicksLeft(10);
        //#if MC>1182
        } else if (shape instanceof Warden warden) {
            warden.attackAnimationState.start(tickCount);
        //#endif
        } else if (shape instanceof Ravager ravager) {
            ((RavagerEntityAccessor) ravager).setAttackTick(10);
        } else if (shape instanceof WitherSkeleton && target instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(MobEffects.WITHER, 200), this);
        } else if (shape instanceof Bee bee && bee.isAngry() && target instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(MobEffects.POISON, 200), this);
        } else if (shape instanceof Pufferfish pufferfish) {
            int i = pufferfish.getPuffState();

            if (target instanceof LivingEntity livingTarget) {
                //#if MC>1182
                if (livingTarget.hurt(this.damageSources().mobAttack((Player) (Object) this), (float) (1 + i))) {
                //#else
                //$$ if (livingTarget.hurt(DamageSource.mobAttack((Player) (Object) this), (float) (1 + i))) {
                //#endif
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

    //#if MC>1182
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
    //#endif

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickFire(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (!CEntity.level(player).isClientSide && !player.isCreative() && !player.isSpectator()) {
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
                        if (CEntity.level(player).isRaining()) {
                            return;
                        }

                        // check for helmets to negate burning
                        ItemStack itemStack = player.getItemBySlot(EquipmentSlot.HEAD);
                        if (!itemStack.isEmpty()) {
                            if (itemStack.isDamageableItem()) {

                                // damage stack instead of burning player
                                itemStack.setDamageValue(itemStack.getDamageValue() + player.getRandom().nextInt(2));
                                if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
                                    //#if MC>1206
                                    player.onEquippedItemBroken(itemStack.getItem(), EquipmentSlot.HEAD);
                                    //#else
                                    //$$ player.broadcastBreakEvent(EquipmentSlot.HEAD);
                                    //#endif
                                    player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                                }
                            }

                            bl = false;
                        }

                        // set player on fire
                        if (bl) {
                            //#if MC>=1205
                            player.igniteForSeconds(8);
                            //#else
                            //$$ player.setSecondsOnFire(8);
                            //#endif
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Unique
    private boolean walkers$isInDaylight() {
        if (CEntity.level(this).isDay() && !CEntity.level(this).isClientSide) {
            //#if MC>1182
            float brightnessAtEyes = getLightLevelDependentMagicValue();
            BlockPos daylightTestPosition = BlockPos.containing(getX(), (double) Math.round(getY()), getZ());
            //#else
            //$$ float brightnessAtEyes = getBrightness();
            //$$ BlockPos daylightTestPosition = new BlockPos(getX(), (double) Math.round(getY()), getZ());
            //#endif

            // move test position up one block for boats
            if (getVehicle() instanceof Boat) {
                daylightTestPosition = daylightTestPosition.above();
            }

            return brightnessAtEyes > 0.5F && random.nextFloat() * 30.0F < (brightnessAtEyes - 0.4F) * 2.0F && CEntity.level(this).canSeeSky(daylightTestPosition);
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
                final boolean couldEnoughToSnow = CEntity.level(this).getBiome(blockPosition()).value().coldEnoughToSnow(blockPosition());
                for (TemperatureTrait<?> temperaturetrait : TraitRegistry.get(shape, TemperatureTrait.ID).stream().map(entry -> (TemperatureTrait<?>) entry).toList()) {
                    if (!temperaturetrait.coldEnoughToSnow == couldEnoughToSnow) {
                        //#if MC>1182
                        player.hurt(CEntity.level(this).damageSources().onFire(), 1.0f);
                        //#else
                        //$$ player.hurt(DamageSource.ON_FIRE, 1.0f);
                        //#endif
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

            if (!CEntity.level(this).isClientSide) {
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
                    ((Fox) shape).setJumping(!CEntity.isOnGround(player));
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
        if (ownPlayer.isAlive() && PlayerShape.getCurrentShape(ownPlayer) instanceof Slime slimeShape && (entity instanceof Player targetPlayer && !(PlayerShape.getCurrentShape(targetPlayer) instanceof Slime))) {
            int i = slimeShape.getSize();
            //#if MC>1182
            boolean wasHurt = targetPlayer.hurt(ownPlayer.damageSources().mobAttack(ownPlayer), (float) ownPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE));
            //#else
            //$$ boolean wasHurt = targetPlayer.hurt(DamageSource.mobAttack(ownPlayer), (float) ownPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE));
            //#endif
            if (this.distanceToSqr(targetPlayer) < 0.6 * (double) i * 0.6 * (double) i && ownPlayer.hasLineOfSight(targetPlayer) && wasHurt) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                //#if MC>1206
                EnchantmentHelper.doPostAttackEffects((ServerLevel) CEntity.level(ownPlayer), targetPlayer, ownPlayer.damageSources().mobAttack(ownPlayer));
                //#else
                //$$ this.doEnchantDamageEffects(ownPlayer, targetPlayer);
                //#endif
            }
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    private void handeReinforcementsTrait(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        LivingEntity shape = PlayerShape.getCurrentShape(player);
        if (source.getEntity() instanceof LivingEntity livingAttacker && shape != null) {
            for (ReinforcementsTrait<LivingEntity> reinforcementTrait : TraitRegistry.get(shape, ReinforcementsTrait.ID).stream().map(trait -> (ReinforcementsTrait<LivingEntity>) trait).toList()) {
                double d = reinforcementTrait.getRange();
                AABB aABB = AABB.unitCubeFromLowerCorner(this.position()).inflate(d, 10.0, d);
                Iterator<? extends LivingEntity> var5 = CEntity.level(this).getEntitiesOfClass(Mob.class, aABB, EntitySelector.NO_SPECTATORS.and(entity -> {
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

    @Inject(method = "hurt", at = @At("HEAD"))
    private void instantDieOnDamageTypeTrait(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);
        if (shape != null) {
            for (ShapeTrait<LivingEntity> instantDieOnDamageTypetrait : TraitRegistry.get(shape, InstantDieOnDamageMsgTrait.ID)) {
                if (source.getMsgId().equals(((InstantDieOnDamageMsgTrait<LivingEntity>) instantDieOnDamageTypetrait).msgId)) {
                    this.kill();
                }
            }
        }
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;tick(Lnet/minecraft/world/entity/player/Player;)V"))
    private boolean preventFoodDataTick(FoodData instance, Player player) {
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
