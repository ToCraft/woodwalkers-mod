package tocraft.walkers.api;

import net.minecraft.world.entity.player.Player;
import tocraft.walkers.impl.PlayerDataProvider;

public class PlayerHostility {

    /**
     * Returns whether the player this component is attached to will be targeted by hostile mobs, regardless of Walkers.
     *
     * <p>Hostility works on a timer, and is set when the player attacks a hostile mob.
     *
     * @return whether this component's player will be targeted by hostile mobs, regardless of Walkers
     */
    public static boolean hasHostility(Player player) {
        return ((PlayerDataProvider) player).walkers$getRemainingHostilityTime() > 0;
    }

    /**
     * Sets this components' hostility timer to the given time in ticks.
     *
     * @param hostilityTime time, in ticks, to set hostility timer to
     */
    public static void set(Player player, int hostilityTime) {
        ((PlayerDataProvider) player).walkers$setRemainingHostilityTime(hostilityTime);
    }
}
