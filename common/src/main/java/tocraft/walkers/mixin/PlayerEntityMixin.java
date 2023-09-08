package tocraft.walkers.mixin;

import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.mixin.accessor.*;
import tocraft.walkers.registry.WalkersEntityTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {

    @Shadow
    public abstract boolean isSpectator();

    @Shadow
    public abstract EntityDimensions getDimensions(EntityPose pose);

    @Shadow
    public abstract boolean isSwimming();

    private PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "getDimensions",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        LivingEntity entity = PlayerShape.getCurrentShape((PlayerEntity) (Object) this);

        if(entity != null) {
            cir.setReturnValue(entity.getDimensions(pose));
        }
    }

    /**
     * When a player turns into an Aquatic shape, they lose breath outside water.
     *
     * @param ci mixin callback info
     */
    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void tickAquaticBreathingOutsideWater(CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((PlayerEntity) (Object) this);

        if(shape != null) {
            if(Walkers.isAquatic(shape)) {
                int air = this.getAir();

                // copy of WaterCreatureEntity#tickWaterBreathingAir
                if(this.isAlive() && !this.isInsideWaterOrBubbleColumn()) {
                    int i = EnchantmentHelper.getRespiration((LivingEntity) (Object) this);

                    // If the player has respiration, 50% chance to not consume air
                    if(i > 0) {
                        if(random.nextInt(i + 1) <= 0) {
                            this.setAir(air - 1);
                        }
                    }

                    // No respiration, decrease air as normal
                    else {
                        this.setAir(air - 1);
                    }

                    // Air has ran out, start drowning
                    if(this.getAir() == -20) {
                        this.setAir(0);
                        this.damage(getDamageSources().fall(), 2.0F);
                    }
                } else {
                    this.setAir(300);
                }
            }
        }
    }

    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    private void shape_getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        // cursed
        try {
            LivingEntity shape = PlayerShape.getCurrentShape((PlayerEntity) (Object) this);

            if(shape != null) {
                cir.setReturnValue(((LivingEntityAccessor) shape).callGetActiveEyeHeight(getPose(), getDimensions(getPose())));
            }
        } catch (Exception ignored) {

        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getEyeHeight(EntityPose pose) {
        LivingEntity shape = PlayerShape.getCurrentShape((PlayerEntity) (Object) this);

        if(shape != null) {
            return shape.getEyeHeight(pose);
        } else {
            return this.getEyeHeight(pose, this.getDimensions(pose));
        }
    }

    @Inject(
            method = "getHurtSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape((PlayerEntity) (Object) this);

        if(WalkersConfig.getInstance().useShapeSounds() && shape != null) {
            cir.setReturnValue(((LivingEntityAccessor) shape).callGetHurtSound(source));
        }
    }


    // todo: separate mixin for ambient sounds
    private int shape_ambientSoundChance = 0;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void tickAmbientSounds(CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((PlayerEntity) (Object) this);

        if(!getWorld().isClient && WalkersConfig.getInstance().playAmbientSounds() && shape instanceof MobEntity) {
            MobEntity mobShape = (MobEntity) shape;

            if(this.isAlive() && this.random.nextInt(1000) < this.shape_ambientSoundChance++) {
                // reset sound delay
                this.shape_ambientSoundChance = -mobShape.getMinAmbientSoundDelay();

                // play ambient sound
                SoundEvent sound = ((MobEntityAccessor) mobShape).callGetAmbientSound();
                if(sound != null) {
                    float volume = ((LivingEntityAccessor) mobShape).callGetSoundVolume();
                    float pitch = ((LivingEntityAccessor) mobShape).callGetSoundPitch();

                    // By default, players can not hear their own ambient noises.
                    // This is because ambient noises can be very annoying.
                    if(WalkersConfig.getInstance().hearSelfAmbient()) {
                        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch);
                    } else {
                        this.getWorld().playSound((PlayerEntity) (Object) this, this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch);
                    }
                }
            }
        }
    }

    @Inject(
            method = "getDeathSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getDeathSound(CallbackInfoReturnable<SoundEvent> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape((PlayerEntity) (Object) this);

        if(WalkersConfig.getInstance().useShapeSounds() && shape != null) {
            cir.setReturnValue(((LivingEntityAccessor) shape).callGetDeathSound());
        }
    }

    @Inject(
            method = "getFallSounds",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getFallSounds(CallbackInfoReturnable<LivingEntity.FallSounds> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape((PlayerEntity) (Object) this);

        if(WalkersConfig.getInstance().useShapeSounds() && shape != null) {
            cir.setReturnValue(shape.getFallSounds());
        }
    }

    @Inject(method = "attack", at = @At("HEAD"))
    protected void shape_tryAttack(Entity target, CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((PlayerEntity) (Object) this);

        if(shape instanceof IronGolemEntity golem) {
            ((IronGolemEntityAccessor) golem).setAttackTicksLeft(10);
        }

        if(shape instanceof WardenEntity warden) {
            warden.attackingAnimationState.start(age);
        }

        if(shape instanceof RavagerEntity ravager) {
            ((RavagerEntityAccessor) ravager).setAttackTick(10);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickGolemAttackTicks(CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((PlayerEntity) (Object) this);

        if(shape instanceof IronGolemEntity golem) {
            IronGolemEntityAccessor accessor = (IronGolemEntityAccessor) golem;
            if(accessor.getAttackTicksLeft() > 0) {
                accessor.setAttackTicksLeft(accessor.getAttackTicksLeft() - 1);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickRavagerAttackTicks(CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((PlayerEntity) (Object) this);

        if(shape instanceof RavagerEntity ravager) {
            RavagerEntityAccessor accessor = (RavagerEntityAccessor) ravager;
            if(accessor.getAttackTick() > 0) {
                accessor.setAttackTick(accessor.getAttackTick() - 1);
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickWardenSneakingAnimation(CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((PlayerEntity) (Object) this);

        if(shape instanceof WardenEntity warden) {
            if(isSneaking()) {
                if(!warden.sniffingAnimationState.isRunning()) {
                    warden.sniffingAnimationState.start(age);
                }
            } else {
                warden.sniffingAnimationState.stop();
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickFire(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if(!player.getWorld().isClient && !player.isCreative() && !player.isSpectator()) {
            // check if the player is shape
            if(shape != null) {
                EntityType<?> type = shape.getType();

                // check if the player's current shape burns in sunlight
                if(type.isIn(WalkersEntityTags.BURNS_IN_DAYLIGHT)) {
                    boolean bl = this.isInDaylight();
                    if(bl) {

                        // Can't burn in the rain
                        if(player.getWorld().isRaining()) {
                            return;
                        }

                        // check for helmets to negate burning
                        ItemStack itemStack = player.getEquippedStack(EquipmentSlot.HEAD);
                        if(!itemStack.isEmpty()) {
                            if(itemStack.isDamageable()) {

                                // damage stack instead of burning player
                                itemStack.setDamage(itemStack.getDamage() + player.getRandom().nextInt(2));
                                if(itemStack.getDamage() >= itemStack.getMaxDamage()) {
                                    player.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                                    player.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                                }
                            }

                            bl = false;
                        }

                        // set player on fire
                        if(bl) {
                            player.setOnFireFor(8);
                        }
                    }
                }
            }
        }
    }

    @Unique
    private boolean isInDaylight() {
        if(getWorld().isDay() && !getWorld().isClient) {
            float brightnessAtEyes = getBrightnessAtEyes();
            BlockPos daylightTestPosition = BlockPos.ofFloored(getX(), (double) Math.round(getY()), getZ());

            // move test position up one block for boats
            if(getVehicle() instanceof BoatEntity) {
                daylightTestPosition = daylightTestPosition.up();
            }

            return brightnessAtEyes > 0.5F && random.nextFloat() * 30.0F < (brightnessAtEyes - 0.4F) * 2.0F && getWorld().isSkyVisible(daylightTestPosition);
        }

        return false;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickTemperature(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if(!player.isCreative() && !player.isSpectator()) {
            // check if the player is shape
            if(shape != null) {
                EntityType<?> type = shape.getType();

                // damage player if they are an shape that gets hurt by high temps (eg. snow golem in nether)
                if(type.isIn(WalkersEntityTags.HURT_BY_HIGH_TEMPERATURE)) {
                    Biome biome = getWorld().getBiome(getBlockPos()).value();
                    if (!biome.isCold(getBlockPos())) {
                        player.damage(getWorld().getDamageSources().onFire(), 1.0f);
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickWalkers(CallbackInfo ci) {
        if(!getWorld().isClient) {
            PlayerEntity player = (PlayerEntity) (Object) this;
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            // assign basic data to entity from player on server; most data transferring occurs on client
            if(shape != null) {
                shape.setPos(player.getX(), player.getY(), player.getZ());
                shape.setHeadYaw(player.getHeadYaw());
                shape.setJumping(((LivingEntityAccessor) player).isJumping());
                shape.setSprinting(player.isSprinting());
                shape.setStuckArrowCount(player.getStuckArrowCount());
                shape.setInvulnerable(true);
                shape.setNoGravity(true);
                shape.setSneaking(player.isSneaking());
                shape.setSwimming(player.isSwimming());
                shape.setCurrentHand(player.getActiveHand());
                shape.setPose(player.getPose());

                if(shape instanceof TameableEntity) {
                    ((TameableEntity) shape).setInSittingPose(player.isSneaking());
                    ((TameableEntity) shape).setSitting(player.isSneaking());
                }

                ((EntityAccessor) shape).shape_callSetFlag(7, player.isFallFlying());

                ((LivingEntityAccessor) shape).callTickActiveItemStack();
                PlayerShape.sync((ServerPlayerEntity) player); // safe cast - context is server world
            }
        }
    }
}
