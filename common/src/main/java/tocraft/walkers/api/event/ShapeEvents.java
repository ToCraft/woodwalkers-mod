package tocraft.walkers.api.event;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import tocraft.craftedcore.events.Event;
import tocraft.craftedcore.events.EventBuilder;
import tocraft.walkers.api.variant.ShapeType;

public interface ShapeEvents {
	Event<UnlockShapeCallback> UNLOCK_SHAPE = EventBuilder.createEventResult(UnlockShapeCallback.class);
	
	Event<ShapeSwapCallback> SWAP_SHAPE = EventBuilder.createEventResult(ShapeSwapCallback.class);

	interface ShapeSwapCallback {
		Event.Result swap(ServerPlayer player, @Nullable LivingEntity to);
	}

	interface UnlockShapeCallback {
		Event.Result unlock(ServerPlayer player, ShapeType type);
	}
}
