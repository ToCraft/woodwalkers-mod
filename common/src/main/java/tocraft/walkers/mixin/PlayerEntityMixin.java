package tocraft.walkers.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
import tocraft.walkers.mixin.accessor.*;
import tocraft.walkers.registry.WalkersEntityTags;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {

    @Shadow
    public abstract boolean isSpectator();

    @Shadow
    public abstract @NotNull EntityDimensions getDimensions(Pose pose);

    @Shadow
    public abstract boolean isSwimming();

    private PlayerEntityMixin(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        LivingEntity entity = PlayerShape.getCurrentShape((Player) (Object) this);

        if (entity != null) {
            cir.setReturnValue(entity.getDimensions(pose));
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
            if (Walkers.isAquatic(shape)) {
                int air = this.getAirSupply();

                // copy of WaterCreatureEntity#tickWaterBreathingAir
                if (this.isAlive() && !this.isInWaterOrBubble()) {
                    int i = EnchantmentHelper.getRespiration((LivingEntity) (Object) this);

                    // If the player has respiration, 50% chance to not consume air
                    if (i > 0) {
                        if (random.nextInt(i + 1) <= 0) {
                            this.setAirSupply(air - 1);
                        }
                    }

                    // No respiration, decrease air as normal
                    else {
                        this.setAirSupply(air - 1);
                    }

                    // Air has run out, start drowning
                    if (this.getAirSupply() == -20) {
                        this.setAirSupply(0);
                        this.hurt(damageSources().fall(), 2.0F);
                    }
                } else {
                    this.setAirSupply(air + 1);
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
                cir.setReturnValue(
                        ((LivingEntityAccessor) shape).callGetEyeHeight(getPose(), getDimensions(getPose())));
            }
        } catch (Exception ignored) {

        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getEyeHeight(Pose pose) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (shape != null) {
            return shape.getEyeHeight(pose);
        } else {
            return this.getEyeHeight(pose, this.getDimensions(pose));
        }
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void getHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (Walkers.CONFIG.useShapeSounds && shape != null) {
            cir.setReturnValue(((LivingEntityAccessor) shape).callGetHurtSound(source));
        }
    }

    // todo: separate mixin for ambient sounds
    @Unique
    private int shape_ambientSoundChance = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickAmbientSounds(CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (!level().isClientSide && Walkers.CONFIG.playAmbientSounds && shape instanceof Mob mobShape) {

            if (this.isAlive() && this.random.nextInt(1000) < this.shape_ambientSoundChance++) {
                // reset sound delay
                this.shape_ambientSoundChance = -mobShape.getAmbientSoundInterval();

                // play ambient sound
                SoundEvent sound = ((MobEntityAccessor) mobShape).callGetAmbientSound();
                if (sound != null) {
                    float volume = ((LivingEntityAccessor) mobShape).callGetSoundVolume();
                    float pitch = ((LivingEntityAccessor) mobShape).callGetVoicePitch();

                    // By default, players can not hear their own ambient noises.
                    // This is because ambient noises can be very annoying.
                    if (Walkers.CONFIG.hearSelfAmbient) {
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), sound,
                                this.getSoundSource(), volume, pitch);
                    } else {
                        this.level().playSound((Player) (Object) this, this.getX(), this.getY(), this.getZ(), sound,
                                this.getSoundSource(), volume, pitch);
                    }
                }
            }
        }
    }

    @Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    private void getDeathSound(CallbackInfoReturnable<SoundEvent> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (Walkers.CONFIG.useShapeSounds && shape != null) {
            cir.setReturnValue(((LivingEntityAccessor) shape).callGetDeathSound());
        }
    }

    @Inject(method = "getFallSounds", at = @At("HEAD"), cancellable = true)
    private void getFallSounds(CallbackInfoReturnable<LivingEntity.Fallsounds> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (Walkers.CONFIG.useShapeSounds && shape != null) {
            cir.setReturnValue(shape.getFallSounds());
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
                if (livingTarget.hurt(this.damageSources().mobAttack((Player) (Object) this), (float)(1 + i))) {
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
                EntityType<?> type = shape.getType();

                // check if the player's current shape burns in sunlight
                if (type.is(WalkersEntityTags.BURNS_IN_DAYLIGHT)) {
                    boolean bl = this.walkers$isInDaylight();
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
            // check if the player is shape
            if (shape != null) {
                EntityType<?> type = shape.getType();

                // damage player if they are a shape that gets hurt by high temps (e.g. snow
                // golem in nether)
                if (type.is(WalkersEntityTags.HURT_BY_HIGH_TEMPERATURE)) {
                    Biome biome = level().getBiome(blockPosition()).value();
                    if (!biome.coldEnoughToSnow(blockPosition())) {
                        player.hurt(level().damageSources().onFire(), 1.0f);
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickWalkers(CallbackInfo ci) {
        if (!level().isClientSide) {
            Player player = (Player) (Object) this;
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            // assign basic data to entity from player on server; most data transferring
            // occurs on client
            if (shape != null) {
                shape.setPosRaw(player.getX(), player.getY(), player.getZ());
                shape.setYHeadRot(player.getYHeadRot());
                shape.setJumping(((LivingEntityAccessor) player).isJumping());
                shape.setSprinting(player.isSprinting());
                shape.setArrowCount(player.getArrowCount());
                shape.setInvulnerable(true);
                shape.setNoGravity(true);
                shape.setShiftKeyDown(player.isShiftKeyDown());
                shape.setSwimming(player.isSwimming());
                shape.startUsingItem(player.getUsedItemHand());
                shape.setPose(player.getPose());

                if (shape instanceof TamableAnimal) {
                    ((TamableAnimal) shape).setInSittingPose(player.isShiftKeyDown());
                    ((TamableAnimal) shape).setOrderedToSit(player.isShiftKeyDown());
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
}
