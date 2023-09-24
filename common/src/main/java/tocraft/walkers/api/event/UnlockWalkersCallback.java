package tocraft.walkers.api.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.server.level.ServerPlayer;
import tocraft.walkers.api.variant.ShapeType;

public interface UnlockWalkersCallback {
    Event<UnlockWalkersCallback> EVENT = EventFactory.createEventResult(UnlockWalkersCallback.class);

    EventResult unlock(ServerPlayer player, ShapeType type);
}
