package dev.tocraft.walkers.eventhandler;

import com.mojang.datafixers.util.Either;
import dev.tocraft.craftedcore.event.common.EntityEvents;
import dev.tocraft.craftedcore.event.common.PlayerEvents;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.api.FlightHelper;
import dev.tocraft.walkers.api.PlayerHostility;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.traits.ShapeTrait;
import dev.tocraft.walkers.traits.TraitRegistry;
import dev.tocraft.walkers.traits.impl.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("resource")
public class WalkersEventHandlers {

    public static void initialize() {
        registerHostilityUpdateHandler();
        registerEntityRidingHandler();
        registerPlayerRidingHandler();
        registerLivingDeathHandler();

        EntityEvents.INTERACT_WITH_PLAYER.register((player, entity, hand) -> {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape != null) {
                for (ShapeTrait<LivingEntity> skill : TraitRegistry.get(shape, CantInteractTrait.ID)) {
                    if (!((CantInteractTrait<LivingEntity>) skill).canInteractWithEntity(entity)) {
                        return InteractionResult.FAIL;
                    }
                }
            }

            return InteractionResult.PASS;
        });

        PlayerEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanillaResult) -> {
            if (TraitRegistry.has(PlayerShape.getCurrentShape(player), NocturnalTrait.ID)) {
                return player.level().isBrightOutside() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        PlayerEvents.SLEEP_FINISHED_TIME.register((level, currentTime, timeAdjustment) -> {
            if (level.isBrightOutside() && !level.getPlayers(player -> player.isSleeping() && TraitRegistry.has(PlayerShape.getCurrentShape(player), NocturnalTrait.ID)).isEmpty()) {
                return Optional.of(13000L);
            } else {
                return Optional.empty();
            }
        });

        PlayerEvents.ALLOW_MONSTERS_NEARBY.register((player, sleepingPos, vanillaResult) -> {
            if (!vanillaResult) {
                LivingEntity shape = PlayerShape.getCurrentShape(player);
                if (shape != null && shape.getType().getCategory().equals(MobCategory.MONSTER)) { // monsters don't care about other monsters
                    return InteractionResult.SUCCESS;
                }
            } else { // test if there is a player disguised as a monster nearby
                Vec3 bedCenter = Vec3.atBottomCenterOf(sleepingPos);
                List<Player> monsterPlayers = player.level().getEntitiesOfClass(Player.class, new AABB(bedCenter.x() - (double)8.0F, bedCenter.y() - (double)5.0F, bedCenter.z() - (double)8.0F, bedCenter.x() + (double)8.0F, bedCenter.y() + (double)5.0F, bedCenter.z() + (double)8.0F), p -> {
                    LivingEntity shape = PlayerShape.getCurrentShape(p);
                    return shape != null && shape.getType().getCategory().equals(MobCategory.MONSTER);
                });
                if (!monsterPlayers.isEmpty()) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        PlayerEvents.AWARD_ADVANCEMENT.register((player, advancement, criterionKey) -> {
            if (Walkers.hasFlyingPermissions(player)) {
                FlightHelper.grantFlightTo(player);
                FlightHelper.updateFlyingSpeed(player);
                player.onUpdateAbilities();
            }
        });

        PlayerEvents.DESTROY_SPEED.register((player, speed) -> {
            float newSpeed = speed;

            if (!player.onGround()) {
                if (TraitRegistry.has(PlayerShape.getCurrentShape(player), FlyingTrait.ID)) {
                    newSpeed *= 5;
                } else if (player.isEyeInFluid(FluidTags.WATER)) {
                    for (ShapeTrait<LivingEntity> aquaticTrait : TraitRegistry.get(PlayerShape.getCurrentShape(player), AquaticTrait.ID)) {
                        if (((AquaticTrait<LivingEntity>) aquaticTrait).isAquatic) {
                            newSpeed *= 5;
                            break;
                        }
                    }
                }
            }

            if (player.isEyeInFluid(FluidTags.WATER)) {
                for (ShapeTrait<LivingEntity> aquaticTrait : TraitRegistry.get(PlayerShape.getCurrentShape(player), AquaticTrait.ID)) {
                    if (((AquaticTrait<LivingEntity>) aquaticTrait).isAquatic) {
                        newSpeed *= 5;
                        break;
                    }
                }
            }
            return newSpeed;
        });
    }

    public static void registerHostilityUpdateHandler() {
        EntityEvents.INTERACT_WITH_PLAYER.register((player, entity, hand) -> {
            if (!player.level().isClientSide() && Walkers.CONFIG.playerCanTriggerHostiles && entity instanceof Monster) {
                PlayerHostility.set(player, Walkers.CONFIG.hostilityTime);
            }

            return InteractionResult.PASS;
        });
    }

    // Players with an equipped Walkers inside the `ravager_riding` entity tag
// should
// be able to ride Ravagers.
    public static void registerEntityRidingHandler() {
        EntityEvents.INTERACT_WITH_PLAYER.register((player, entity, hand) -> {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape != null && entity instanceof LivingEntity livingEntity) {
                // checks, if selected entity is rideable
                for (RiderTrait<?> riderTrait : TraitRegistry.get(shape, RiderTrait.ID).stream().map(entry -> (RiderTrait<?>) entry).toList()) {
                    if (riderTrait.isRideable(livingEntity) || (livingEntity instanceof Player rideablePlayer && riderTrait.isRideable(PlayerShape.getCurrentShape(rideablePlayer)))) {
                        player.startRiding(entity);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }

    // make this server-side
    public static void registerPlayerRidingHandler() {
        EntityEvents.INTERACT_WITH_PLAYER.register((player, entity, hand) -> {
            if (entity instanceof Player playerToBeRidden) {
                if (PlayerShape.getCurrentShape(playerToBeRidden) instanceof AbstractHorse) {
                    player.startRiding(playerToBeRidden, true, false);
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        });
    }

    public static void registerLivingDeathHandler() {
        EntityEvents.LIVING_DEATH.register((entity, damageSource) -> {
            if (!entity.level().isClientSide()) {
                if (entity instanceof Villager villager && damageSource.getEntity() instanceof Player player && PlayerShape.getCurrentShape(player) instanceof Zombie) {
                    if (!(player.level().getDifficulty() != Difficulty.HARD && player.getRandom().nextBoolean())) {
                        villager.convertTo(EntityType.ZOMBIE_VILLAGER, ConversionParams.single(villager, false, false), zombieVillager -> {
                            zombieVillager.finalizeSpawn((ServerLevelAccessor) player.level(), ((net.minecraft.server.level.ServerLevel) player.level()).getCurrentDifficultyAt(zombieVillager.blockPosition()), EntitySpawnReason.CONVERSION, new Zombie.ZombieGroupData(false, true));
                            zombieVillager.setTradeOffers(villager.getOffers());
                            zombieVillager.setVillagerData(villager.getVillagerData());
                            zombieVillager.setGossips(villager.getGossips().copy());
                            zombieVillager.setVillagerXp(villager.getVillagerXp());
                        });
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }
}
