package tocraft.walkers.api;

import net.minecraft.server.level.ServerPlayer;

public class FlightHelper {

    public static void grantFlightTo(ServerPlayer player) {
        player.getAbilities().mayfly = true;
    }

    public static boolean hasFlight(ServerPlayer player) {
        return player.getAbilities().mayfly;
    }

    public static void revokeFlight(ServerPlayer player) {
        if (player.gameMode.isSurvival()) {
            player.getAbilities().mayfly = false;
        }

        player.getAbilities().flying = false;
    }
}
