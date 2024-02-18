package tocraft.walkers.registry;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerHostility;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.impl.PlayerDataProvider;

public class WalkersEventHandlers {

    public static void initialize() {
        WalkersEventHandlers.registerHostilityUpdateHandler();
        WalkersEventHandlers.registerRavagerRidingHandler();
        WalkersEventHandlers.registerHostileHorseRidingHandler();
        WalkersEventHandlers.registerPlayerRidingHandler();
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
}
