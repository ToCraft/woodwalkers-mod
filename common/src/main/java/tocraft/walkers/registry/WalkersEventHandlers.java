package tocraft.walkers.registry;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerHostility;
import tocraft.walkers.api.PlayerShape;

public class WalkersEventHandlers {

	public static void initialize() {
		WalkersEventHandlers.registerHostilityUpdateHandler();
		WalkersEventHandlers.registerRavagerRidingHandler();
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
			if (entity instanceof Ravager) {
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
}
