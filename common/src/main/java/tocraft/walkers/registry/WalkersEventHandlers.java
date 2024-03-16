package tocraft.walkers.registry;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ServerLevelAccessor;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerHostility;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.skills.SkillRegistry;
import tocraft.walkers.api.skills.impl.BurnInDaylightSkill;
import tocraft.walkers.api.skills.impl.FlyingSkill;
import tocraft.walkers.impl.PlayerDataProvider;

public class WalkersEventHandlers {

    public static void initialize() {
        registerHostilityUpdateHandler();
        registerRavagerRidingHandler();
        registerHostileHorseRidingHandler();
        registerPlayerRidingHandler();
        registerLivingDeathHandler();
        registerHandlerForDeprecatedEntityTags();
    }

    @SuppressWarnings("unchecked")
    public static void registerHandlerForDeprecatedEntityTags() {
        LifecycleEvent.SERVER_LEVEL_LOAD.register((serverLevel) -> {
            for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
                if (entityType.is(WalkersEntityTags.BURNS_IN_DAYLIGHT)) {
                    SkillRegistry.register((EntityType<LivingEntity>) entityType, new BurnInDaylightSkill<>());
                }
                if (entityType.is(WalkersEntityTags.FLYING)) {
                    SkillRegistry.register((EntityType<LivingEntity>) entityType, new FlyingSkill<>());
                }
            }
        });
    }

    public static void registerHostilityUpdateHandler() {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            if (!player.level().isClientSide && entity instanceof Monster) {
                PlayerHostility.set(player, Walkers.CONFIG.hostilityTime);
            }

            return EventResult.pass();
        });
    }

    // Players with an equipped Walkers inside the `ravager_riding` entity tag
    // should
    // be able to ride Ravagers.
    public static void registerRavagerRidingHandler() {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            // checks, if selected entity is a Ravager or a Player, shaped as a Ravager
            if (entity instanceof Ravager || entity instanceof Player targetedPlayer && ((PlayerDataProvider) targetedPlayer).walkers$getCurrentShape() instanceof Ravager) {
                LivingEntity shape = PlayerShape.getCurrentShape(player);
                if (shape != null) {
                    if (shape.getType().is(WalkersEntityTags.RAVAGER_RIDING)) {
                        player.startRiding(entity);
                    }
                }
            }

            return EventResult.pass();
        });
    }

    // hostile players should be able to ride hostile horses
    public static void registerHostileHorseRidingHandler() {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            // checks, if selected entity is a Ravager or a Player, shaped as a Ravager
            if (entity instanceof SkeletonHorse || entity instanceof ZombieHorse) {
                LivingEntity shape = PlayerShape.getCurrentShape(player);

                if (shape instanceof Enemy) {
                    player.startRiding(entity);
                }
            }

            return EventResult.pass();
        });
    }

    // make this server-side
    public static void registerPlayerRidingHandler() {
        InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
            if (entity instanceof Player playerToBeRidden) {
                if (PlayerShape.getCurrentShape(playerToBeRidden) instanceof AbstractHorse) {
                    player.startRiding(playerToBeRidden, true);
                }
            }
            return EventResult.pass();
        });
    }

    public static void registerLivingDeathHandler() {
        EntityEvent.LIVING_DEATH.register((entity, damageSource) -> {
            if (!entity.level().isClientSide()) {
                if (entity instanceof Villager villager && damageSource.getEntity() instanceof Player player && PlayerShape.getCurrentShape(player) instanceof Zombie) {
                    if (!(player.level().getDifficulty() != Difficulty.HARD && player.getRandom().nextBoolean())) {
                        ZombieVillager zombievillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
                        if (zombievillager != null) {
                            zombievillager.finalizeSpawn((ServerLevelAccessor) player.level(), player.level().getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), null);
                            zombievillager.setVillagerData(villager.getVillagerData());
                            zombievillager.setGossips(villager.getGossips().store(NbtOps.INSTANCE));
                            zombievillager.setTradeOffers(villager.getOffers().createTag());
                            zombievillager.setVillagerXp(villager.getVillagerXp());
                        }
                    }
                }
            }
            return EventResult.pass();
        });
    }
}
