package tocraft.walkers.mixin.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.ability.impl.GrassEaterAbility;
import tocraft.walkers.api.PlayerAbilities;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.WalkersTickHandler;
import tocraft.walkers.api.WalkersTickHandlers;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.impl.ShapeDataProvider;
import tocraft.walkers.mixin.accessor.DolphinAccessor;
import tocraft.walkers.mixin.accessor.PufferfishAccessor;
import tocraft.walkers.mixin.accessor.SheepAccessor;
import tocraft.walkers.network.impl.VehiclePackets;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.MobEffectSkill;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("ConstantConditions")
@Mixin(Player.class)
public abstract class PlayerEntityTickMixin extends LivingEntity {

    private PlayerEntityTickMixin(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
    @Inject(method = "tick", at = @At("HEAD"))
    private void serverTick(CallbackInfo info) {
        // Tick WalkersTickHandlers on the client & server.
        @Nullable LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);
        if (shape != null) {
            @Nullable WalkersTickHandler handler = WalkersTickHandlers.getHandlers().get(shape.getType());
            if (handler != null) {
                handler.tick((Player) (Object) this, shape);
            }
        }

        // Update misc. server-side entity properties for the player.
        if (!level().isClientSide) {
            PlayerDataProvider data = (PlayerDataProvider) this;
            data.walkers$setRemainingHostilityTime(Math.max(0, data.walkers$getRemainingHostilityTime() - 1));

            // Update cooldown & Sync
            ServerPlayer player = (ServerPlayer) (Object) this;
            PlayerAbilities.setCooldown(player, Math.max(0, data.walkers$getAbilityCooldown() - 1));
            PlayerAbilities.sync(player);

            VehiclePackets.sync((ServerPlayer) (Object) this);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void setShapeData(CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);
        if (shape instanceof ShapeDataProvider shapeData) {
            if (!shapeData.walkers$isShape()) {
                shapeData.walkers$setIsShape(true);
            }
            shapeData.walkers$setPlayerDamageSource(this.damageSources().playerAttack((Player) (Object) this));
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void pufferfishServerTick(CallbackInfo info) {
        if (!level().isClientSide && this.isAlive()) {
            LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);
            if (shape instanceof Pufferfish pufferfishShape) {
                if (((PufferfishAccessor) pufferfishShape).getInflateCounter() > 0) {
                    if (pufferfishShape.getPuffState() == 0) {
                        this.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getVoicePitch());
                        pufferfishShape.setPuffState(1);
                    } else if (((PufferfishAccessor) pufferfishShape).getInflateCounter() > 40 && pufferfishShape.getPuffState() == 1) {
                        this.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getVoicePitch());
                        pufferfishShape.setPuffState(2);
                    }

                    ((PufferfishAccessor) pufferfishShape).setInflateCounter(((PufferfishAccessor) pufferfishShape).getInflateCounter() + 1);
                } else if (pufferfishShape.getPuffState() != 0) {
                    if (((PufferfishAccessor) pufferfishShape).getDeflateTimer() > 60 && pufferfishShape.getPuffState() == 2) {
                        this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getVoicePitch());
                        pufferfishShape.setPuffState(1);
                    } else if (((PufferfishAccessor) pufferfishShape).getDeflateTimer() > 100 && pufferfishShape.getPuffState() == 1) {
                        this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getVoicePitch());
                        pufferfishShape.setPuffState(0);
                    }

                    ((PufferfishAccessor) pufferfishShape).setDeflateTimer(((PufferfishAccessor) pufferfishShape).getDeflateTimer() + 1);
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void dolphinServerTick(CallbackInfo info) {
        if (!level().isClientSide && this.isAlive()) {
            Player player = (Player) (Object) this;
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape instanceof Dolphin) {
                Player nearestPlayer = player.level().getNearestPlayer(((DolphinAccessor) shape).getSWIM_WITH_PLAYER_TARGETING(), player);
                if (nearestPlayer != null && nearestPlayer.isSwimming()) {
                    nearestPlayer.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100), player);
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void applyMobEffectSkill(CallbackInfo info) {
        if (!level().isClientSide && this.isAlive()) {
            Player player = (Player) (Object) this;
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape != null && SkillRegistry.has(shape, MobEffectSkill.ID)) {
                List<MobEffectSkill<LivingEntity>> skillList = SkillRegistry.get(shape, MobEffectSkill.ID).stream().map(skill -> (MobEffectSkill<LivingEntity>) skill).toList();
                for (MobEffectSkill<LivingEntity> mobEffectSkill : skillList) {
                    MobEffectInstance mobEffectInstance = mobEffectSkill.mobEffectInstance;
                    // apply to self
                    if (mobEffectSkill.showInInventory && mobEffectSkill.applyToSelf) {
                        player.addEffect(new MobEffectInstance(mobEffectInstance.getEffect(), mobEffectInstance.getDuration(), mobEffectInstance.getAmplifier(), mobEffectInstance.isAmbient(), mobEffectInstance.isVisible(), mobEffectInstance.showIcon()), player);
                    }
                    // apply to nearby
                    switch (mobEffectSkill.applyToNearby) {
                        case 0 -> {
                            List<Player> nearbyPlayers = player.level().getNearbyPlayers(TargetingConditions.forNonCombat().range(mobEffectSkill.maxDistanceForEntities).ignoreLineOfSight(), player, player.getBoundingBox().inflate(mobEffectSkill.maxDistanceForEntities, mobEffectSkill.maxDistanceForEntities, mobEffectSkill.maxDistanceForEntities));
                            if (!nearbyPlayers.isEmpty()) {
                                for (int i = 0; i < nearbyPlayers.size() && (mobEffectSkill.amountOfEntitiesToApplyTo < 0 || i < mobEffectSkill.amountOfEntitiesToApplyTo); i++) {
                                    nearbyPlayers.get(i).addEffect(new MobEffectInstance(mobEffectInstance.getEffect(), mobEffectInstance.getDuration(), mobEffectInstance.getAmplifier(), mobEffectInstance.isAmbient(), mobEffectInstance.isVisible(), mobEffectInstance.showIcon()), player);
                                }
                            }
                        }
                        case 1 -> {
                            List<Mob> nearbyMobs = player.level().getNearbyEntities(Mob.class, TargetingConditions.forNonCombat().range(mobEffectSkill.maxDistanceForEntities).ignoreLineOfSight(), player, player.getBoundingBox().inflate(mobEffectSkill.maxDistanceForEntities, mobEffectSkill.maxDistanceForEntities, mobEffectSkill.maxDistanceForEntities));
                            if (!nearbyMobs.isEmpty()) {
                                for (int i = 0; i < nearbyMobs.size() && (mobEffectSkill.amountOfEntitiesToApplyTo < 0 || i < mobEffectSkill.amountOfEntitiesToApplyTo); i++) {
                                    nearbyMobs.get(i).addEffect(new MobEffectInstance(mobEffectInstance.getEffect(), mobEffectInstance.getDuration(), mobEffectInstance.getAmplifier(), mobEffectInstance.isAmbient(), mobEffectInstance.isVisible(), mobEffectInstance.showIcon()), player);
                                }
                            }
                        }
                        case 2 -> {
                            List<Mob> nearbyMobs = player.level().getNearbyEntities(Mob.class, TargetingConditions.forNonCombat().range(mobEffectSkill.maxDistanceForEntities).ignoreLineOfSight(), player, player.getBoundingBox().inflate(mobEffectSkill.maxDistanceForEntities, mobEffectSkill.maxDistanceForEntities, mobEffectSkill.maxDistanceForEntities));
                            List<Player> nearbyPlayers = player.level().getNearbyPlayers(TargetingConditions.forNonCombat().range(mobEffectSkill.maxDistanceForEntities).ignoreLineOfSight(), player, player.getBoundingBox().inflate(mobEffectSkill.maxDistanceForEntities, mobEffectSkill.maxDistanceForEntities, mobEffectSkill.maxDistanceForEntities));
                            List<LivingEntity> nearbyEntites = new ArrayList<>();
                            nearbyEntites.addAll(nearbyMobs);
                            nearbyEntites.addAll(nearbyPlayers);
                            // sort after distance
                            nearbyEntites.sort((first, second) -> Float.compare(player.distanceTo(first), player.distanceTo(second)));
                            if (!nearbyEntites.isEmpty()) {
                                for (int i = 0; i < nearbyEntites.size() && (mobEffectSkill.amountOfEntitiesToApplyTo < 0 || i < mobEffectSkill.amountOfEntitiesToApplyTo); i++) {
                                    nearbyMobs.get(i).addEffect(new MobEffectInstance(mobEffectInstance.getEffect(), mobEffectInstance.getDuration(), mobEffectInstance.getAmplifier(), mobEffectInstance.isAmbient(), mobEffectInstance.isVisible(), mobEffectInstance.showIcon()), player);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Unique
    private static Predicate<BlockState> walkers$IS_TALL_GRASS = null;

    @Inject(method = "tick", at = @At("HEAD"))
    private void sheepServerTick(CallbackInfo info) {
        if (walkers$IS_TALL_GRASS == null)
            walkers$IS_TALL_GRASS = BlockStatePredicate.forBlock(level().registryAccess().registryOrThrow(Registries.BLOCK).get(new ResourceLocation("grass")));

        if (!level().isClientSide && this.isAlive()) {
            ServerPlayer serverPlayer = (ServerPlayer) (Object) this;
            LivingEntity shape = PlayerShape.getCurrentShape(serverPlayer);
            if (shape != null && AbilityRegistry.get(shape) instanceof GrassEaterAbility<?> grassEaterAbility) {
                if (grassEaterAbility.eatTick.get(serverPlayer.getUUID()) != null && grassEaterAbility.eatTick.get(serverPlayer.getUUID()) != 0) {
                    grassEaterAbility.eatTick.put(serverPlayer.getUUID(), Math.max(0, grassEaterAbility.eatTick.get(serverPlayer.getUUID()) - 1));

                    if (shape instanceof Sheep sheepShape)
                        ((SheepAccessor) sheepShape).setEatAnimationTick(grassEaterAbility.eatTick.get(serverPlayer.getUUID()));

                    if (grassEaterAbility.eatTick.get(serverPlayer.getUUID()) == Mth.positiveCeilDiv(4, 2)) {
                        BlockPos blockPos = serverPlayer.blockPosition();
                        if (walkers$IS_TALL_GRASS.test(level().getBlockState(blockPos)) && walkers$isLookingAtPos(blockPos)) {
                            level().destroyBlock(blockPos, false);

                            gameEvent(GameEvent.EAT);
                            serverPlayer.getFoodData().eat(3, 0.2F);

                            if (shape instanceof Sheep sheepShape) sheepShape.setSheared(false);
                        } else {
                            BlockPos blockPos2 = blockPos.below();
                            if (level().getBlockState(blockPos2).is(Blocks.GRASS_BLOCK) && walkers$isLookingAtPos(blockPos2)) {
                                level().levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos2, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                                level().setBlock(blockPos2, Blocks.DIRT.defaultBlockState(), 2);

                                gameEvent(GameEvent.EAT);
                                serverPlayer.getFoodData().eat(3, 0.1F);

                                if (shape instanceof Sheep sheepShape) sheepShape.setSheared(false);
                            }
                        }

                    }
                }
            }
        }
    }

    @Unique
    private boolean walkers$isLookingAtPos(BlockPos blockPos) {
        Player player = (Player) (Object) this;
        return player.pick(2, 0, false) instanceof BlockHitResult blockHitResult && blockHitResult.getBlockPos().equals(blockPos);
    }
}
