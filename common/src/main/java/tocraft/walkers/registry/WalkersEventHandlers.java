package tocraft.walkers.registry;

import net.minecraft.nbt.NbtOps;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import tocraft.craftedcore.event.common.EntityEvents;
import tocraft.craftedcore.event.common.PlayerEvents;
import tocraft.craftedcore.event.common.ResourceEvents;
import tocraft.craftedcore.patched.CEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.FlightHelper;
import tocraft.walkers.api.PlayerHostility;
import tocraft.walkers.api.PlayerShape;
//#if MC>=1205
import tocraft.walkers.impl.variant.WolfTypeProvider;
//#endif
import tocraft.walkers.traits.ShapeTrait;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.CantInteractTrait;
import tocraft.walkers.traits.impl.NocturnalTrait;
import tocraft.walkers.traits.impl.RiderTrait;

@SuppressWarnings("resource")
public class WalkersEventHandlers {

    public static void initialize() {
        registerHostilityUpdateHandler();
        registerEntityRidingHandler();
        registerPlayerRidingHandler();
        registerLivingDeathHandler();

        // set WolfTypeProvider Range when on server
        //#if MC>=1205
        ResourceEvents.DATA_PACK_SYNC.register(player -> WolfTypeProvider.setRange(CEntity.level(player)));
        //#endif

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
                return CEntity.level(player).isDay() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        PlayerEvents.SLEEP_FINISHED_TIME.register((level, newTime) -> {
            if (level.isDay() && !level.getPlayers(player -> player.isSleeping() && TraitRegistry.has(PlayerShape.getCurrentShape(player), NocturnalTrait.ID)).isEmpty()) {
                return newTime + level.getDayTime() % 24000L > 12000L ? 13000 : -11000;
            } else {
                return newTime;
            }
        });

        PlayerEvents.AWARD_ADVANCEMENT.register((player, advancement, criterionKey) -> {
            if (Walkers.hasFlyingPermissions(player)) {
                FlightHelper.grantFlightTo(player);
                FlightHelper.updateFlyingSpeed(player);
                player.onUpdateAbilities();
            }
        });
    }

    public static void registerHostilityUpdateHandler() {
        EntityEvents.INTERACT_WITH_PLAYER.register((player, entity, hand) -> {
            if (!CEntity.level(player).isClientSide && Walkers.CONFIG.playerCanTriggerHostiles && entity instanceof Monster) {
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
                        return InteractionResult.PASS;
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
                    player.startRiding(playerToBeRidden, true);
                }
            }
            return InteractionResult.PASS;
        });
    }

    public static void registerLivingDeathHandler() {
        EntityEvents.LIVING_DEATH.register((entity, damageSource) -> {
            if (!CEntity.level(entity).isClientSide()) {
                if (entity instanceof Villager villager && damageSource.getEntity() instanceof Player player && PlayerShape.getCurrentShape(player) instanceof Zombie) {
                    if (!(CEntity.level(player).getDifficulty() != Difficulty.HARD && player.getRandom().nextBoolean())) {
                        ZombieVillager zombievillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
                        if (zombievillager != null) {
                            //#if MC>=1205
                            zombievillager.finalizeSpawn((ServerLevelAccessor) CEntity.level(player), CEntity.level(player).getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true));
                            zombievillager.setTradeOffers(villager.getOffers());
                            //#else
                            //$$ zombievillager.finalizeSpawn((ServerLevelAccessor) CEntity.level(player), CEntity.level(player).getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), null);
                            //$$ zombievillager.setTradeOffers(villager.getOffers().createTag());
                            //#endif
                            zombievillager.setVillagerData(villager.getVillagerData());
                            //#if MC>1182
                            zombievillager.setGossips(villager.getGossips().store(NbtOps.INSTANCE));
                            //#else
                            //$$ zombievillager.setGossips(villager.getGossips().store(NbtOps.INSTANCE).getValue());
                            //#endif
                            zombievillager.setVillagerXp(villager.getVillagerXp());
                        }
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }
}
