package tocraft.walkers.api.event;

import net.minecraft.server.level.ServerPlayer;
import tocraft.craftedcore.events.Event;
import tocraft.craftedcore.events.EventBuilder;
import tocraft.walkers.api.variant.ShapeType;

public interface UnlockWalkersCallback {
    Event<UnlockWalkersCallback> EVENT = EventBuilder.createEventResult(UnlockWalkersCallback.class);

    Event.Result unlock(ServerPlayer player, ShapeType type);
}
