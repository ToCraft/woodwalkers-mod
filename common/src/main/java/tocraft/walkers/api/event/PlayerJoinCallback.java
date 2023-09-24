package tocraft.walkers.api.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

/**
 * This event is called when a player joins the server.
 */
public interface PlayerJoinCallback {
    Event<PlayerJoinCallback> EVENT = EventFactory.createLoop(PlayerJoinCallback.class);

    void onPlayerJoin(ServerPlayer player);
}

