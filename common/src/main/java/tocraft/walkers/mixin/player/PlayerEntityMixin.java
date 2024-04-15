package tocraft.walkers.mixin.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.mixin.LivingEntityMixin;
import tocraft.walkers.mixin.accessor.EntityAccessor;
import tocraft.walkers.mixin.accessor.IronGolemEntityAccessor;
import tocraft.walkers.mixin.accessor.LivingEntityAccessor;
import tocraft.walkers.mixin.accessor.RavagerEntityAccessor;
import tocraft.walkers.skills.ShapeSkill;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.*;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("ConstantConditions")
@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {

    @Shadow
    public abstract boolean isSpectator();

    @Shadow
    public abstract @NotNull EntityDimensions getDimensions(Pose pose);

    @Shadow
    public abstract boolean isSwimming();

    @Shadow
    public abstract void die(DamageSource damageSource);

    private PlayerEntityMixin(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        LivingEntity entity = PlayerShape.getCurrentShape((Player) (Object) this);

        if (entity != null) {
            EntityDimensions shapeDimensions = entity.getDimensions(pose);
            if (pose == Pose.CROUCHING && SkillRegistry.has(entity, HumanoidSkill.ID)) {
                cir.setReturnValue(EntityDimensions.scalable(shapeDimensions.width, shapeDimensions.height * 1.5F / 1.8F));
            } else {
                cir.setReturnValue(shapeDimensions);
            }
        }
    }

    /**
     * When a player turns into an Aquatic shape, they lose breath outside water.
     *
     * @param ci mixin callback info
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void tickAquaticBreathingOutsideWater(CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (shape != null) {
            int isAquatic = Walkers.isAquatic(shape);
            if (isAquatic < 2) {
                int air = this.getAirSupply();
                // copy of WaterCreatureEntity#tickWaterBreathingAir
                if (this.isAlive() && !this.isInWaterOrBubble()) {
                    if (isAquatic < 1) {
                        int i = EnchantmentHelper.getRespiration((LivingEntity) (Object) this);

                        // If the player has respiration, 50% chance to not consume air
                        if (i > 0) {
                            if (random.nextInt(i + 1) <= 0) {
                                this.setAirSupply(this.decreaseAirSupply(air));
                            }
                        }

                        // No respiration, decrease air as normal
                        else {
                            this.setAirSupply(this.decreaseAirSupply(air));
                        }

                        // Air has run out, start drowning
                        if (this.getAirSupply() == -20) {
                            this.setAirSupply(0);
                            this.hurt(damageSources().dryOut(), 2.0F);
                        }
                    }
                } else if (this.getAirSupply() < this.getMaxAirSupply()) {
                    this.setAirSupply(this.increaseAirSupply(air));
                }
            }
        }
    }

    @Inject(method = "getStandingEyeHeight", at = @At("HEAD"), cancellable = true)
    private void shape_getStandingEyeHeight(Pose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        // cursed
        try {
            LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

            if (shape != null) {
                cir.setReturnValue(((LivingEntityAccessor) shape).callGetEyeHeight(getPose(), getDimensions(getPose())));
            }
        } catch (Exception ignored) {

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
        } else if (shape instanceof Pufferfish pufferfish) {
            int i = pufferfish.getPuffState();

            if (target instanceof LivingEntity livingTarget) {
                if (livingTarget.hurt(this.damageSources().mobAttack((Player) (Object) this), (float) (1 + i))) {
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
                if (SkillRegistry.has(shape, BurnInDaylightSkill.ID)) {
                    boolean bl = this.walkers$isInDaylight();
                    // handle night burning
                    for (BurnInDaylightSkill<?> skill : SkillRegistry.get(shape, BurnInDaylightSkill.ID).stream().map(skill -> ((BurnInDaylightSkill<?>) skill)).toList()) {
                        bl = (bl && !skill.burnInMoonlightInstead) || (!this.walkers$isInDaylight() && skill.burnInMoonlightInstead);
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
                                    player.broadcastBreakEvent(EquipmentSlot.HEAD);
                                    player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                                }
                            }

                            bl = false;
                        }

                        // set player on fire
                        if (bl) {
                            player.setSecondsOnFire(8);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Unique
    private boolean walkers$isInDaylight() {
        if (level().isDay() && !level().isClientSide) {
            float brightnessAtEyes = getLightLevelDependentMagicValue();
            BlockPos daylightTestPosition = BlockPos.containing(getX(), (double) Math.round(getY()), getZ());

            // move test position up one block for boats
            if (getVehicle() instanceof Boat) {
                daylightTestPosition = daylightTestPosition.above();
            }

            return brightnessAtEyes > 0.5F && random.nextFloat() * 30.0F < (brightnessAtEyes - 0.4F) * 2.0F
                    && level().canSeeSky(daylightTestPosition);
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
                final boolean couldEnoughToSnow = level().getBiome(blockPosition()).value().coldEnoughToSnow(blockPosition());
                for (TemperatureSkill<?> temperatureSkill : SkillRegistry.get(shape, TemperatureSkill.ID).stream().map(entry -> (TemperatureSkill<?>) entry).toList()) {
                    if (!temperatureSkill.coldEnoughToSnow == couldEnoughToSnow) {
                        player.hurt(level().damageSources().onFire(), 1.0f);
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

            if (!level().isClientSide) {
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
        if (ownPlayer.isAlive() && PlayerShape.getCurrentShape(ownPlayer) instanceof Slime slimeShape && (entity instanceof Player targetPlayer && !(PlayerShape.getCurrentShape(targetPlayer) instanceof Slime))) {
            int i = slimeShape.getSize();
            if (this.distanceToSqr(targetPlayer) < 0.6 * (double) i * 0.6 * (double) i
                    && ownPlayer.hasLineOfSight(targetPlayer)
                    && targetPlayer.hurt(ownPlayer.damageSources().mobAttack(ownPlayer), (float) ownPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.doEnchantDamageEffects(ownPlayer, targetPlayer);
            }
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    private void handeReinforcementsSkill(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        LivingEntity shape = PlayerShape.getCurrentShape(player);
        if (source.getEntity() instanceof LivingEntity livingAttacker && shape != null) {
            for (ShapeSkill<LivingEntity> reinforcementSkill : SkillRegistry.get(shape, ReinforcementsSkill.ID)) {
                double d = ((ReinforcementsSkill<LivingEntity>) reinforcementSkill).range;
                List<EntityType<?>> reinforcements = ((ReinforcementsSkill<LivingEntity>) reinforcementSkill).reinforcements;
                List<TagKey<EntityType<?>>> reinforcementTags = ((ReinforcementsSkill<LivingEntity>) reinforcementSkill).reinforcementTags;
                AABB aABB = AABB.unitCubeFromLowerCorner(this.position()).inflate(d, 10.0, d);
                Iterator<? extends LivingEntity> var5 = this.level().getEntitiesOfClass(Mob.class, aABB, EntitySelector.NO_SPECTATORS.and(entity -> {
                    boolean bool = false;
                    for (TagKey<EntityType<?>> reinforcementTag : reinforcementTags) {
                        if (entity.getType().is(reinforcementTag)) bool = true;
                        break;
                    }
                    return reinforcements.contains(entity.getType()) || ((reinforcements.isEmpty() && shape.getClass().isInstance(entity)) || bool);
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
    private void instantDieOnDamageTypeSkill(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);
        if (shape != null) {
            for (ShapeSkill<LivingEntity> instantDieOnDamageTypeSkill : SkillRegistry.get(shape, InstantDieOnDamageTypeSkill.ID)) {
                if (source.type() == level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).get(((InstantDieOnDamageTypeSkill<LivingEntity>) instantDieOnDamageTypeSkill).damageType)) {
                    this.die(source);
                }
            }
        }
    }
}
