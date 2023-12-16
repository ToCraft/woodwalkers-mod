package tocraft.walkers.api;

import net.minecraft.server.level.ServerPlayer;
import tocraft.walkers.mixin.accessor.PlayerEntityAccessor;

public class FlightHelper {

    public static void grantFlightTo(ServerPlayer player) {
    	((PlayerEntityAccessor) player).getAbilities().mayfly = true;
    }

    public static boolean hasFlight(ServerPlayer player) {
        return ((PlayerEntityAccessor) player).getAbilities().mayfly;
    }

    public static void revokeFlight(ServerPlayer player) {
        if(player.gameMode.isSurvival()) {
        	((PlayerEntityAccessor) player).getAbilities().mayfly = false;
        }

        ((PlayerEntityAccessor) player).getAbilities().flying = false;
    }
}
