package tocraft.walkers.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import tocraft.craftedcore.event.Event;
import tocraft.craftedcore.event.EventFactory;
import tocraft.walkers.Walkers;

@FunctionalInterface
public interface FlightHelper {
    Event<FlightHelper> GRANT = EventFactory.createWithInteractionResult();
    Event<FlightHelper> REVOKE = EventFactory.createWithInteractionResult();

    InteractionResult event(ServerPlayer player);

    static void grantFlightTo(ServerPlayer player) {
        if (!GRANT.invoke().event(player).consumesAction()) {
            player.getAbilities().mayfly = true;
        }
    }

    static boolean hasFlight(ServerPlayer player) {
        return player.getAbilities().mayfly;
    }

    static void updateFlyingSpeed(Player player) {
        player.getAbilities().setFlyingSpeed(PlayerShape.getCurrentShape(player) != null ? Walkers.CONFIG.flySpeed : 0.05F);
    }

    static void revokeFlight(ServerPlayer player) {
        if (!REVOKE.invoke().event(player).consumesAction()) {
            if (player.gameMode.isSurvival()) {
                player.getAbilities().mayfly = false;
            }

            player.getAbilities().flying = false;
        }
    }
}
