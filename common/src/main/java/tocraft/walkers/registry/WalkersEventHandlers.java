package tocraft.walkers.registry;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
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
		WalkersEventHandlers.registerPlayerRidingHandler();
	}

	public static void registerHostilityUpdateHandler() {
		InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
			if (!player.level().isClientSide && entity instanceof Monster) {
				PlayerHostility.set(player, Walkers.CONFIG.hostilityTime());
			}

			return EventResult.pass();
		});
	}

	// Players with an equipped Walkers inside the `ravager_riding` entity tag
	// should
	// be able to ride Ravagers.
	public static void registerRavagerRidingHandler() {
		InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
			// checks, if selected entity is a Ravager or a Player, shaped as a Rvager
			if (entity instanceof Ravager || entity instanceof Player targetedPlayer && ((PlayerDataProvider) targetedPlayer).getCurrentShape() instanceof Ravager) {
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
	
	// make this server-side
	public static void registerPlayerRidingHandler() {
		InteractionEvent.INTERACT_ENTITY.register((player, entity, hand) -> {
			if (entity instanceof Player playerToBeRidden) {
				if (((PlayerDataProvider) playerToBeRidden).getCurrentShape() instanceof AbstractHorse) {
					player.startRiding(playerToBeRidden, true);
				}
			}
			return EventResult.pass();
		});
	}
}
