package tocraft.walkers.api.event;

import net.minecraft.server.level.ServerPlayer;
import tocraft.craftedcore.events.Event;
import tocraft.craftedcore.events.EventBuilder;

/**
 * This event is called when a player joins the server.
 */
public interface PlayerJoinCallback {
    Event<PlayerJoinCallback> EVENT = EventBuilder.createLoop(PlayerJoinCallback.class);

    void onPlayerJoin(ServerPlayer player);
}

