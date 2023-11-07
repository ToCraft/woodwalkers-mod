package tocraft.walkers.api;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import tocraft.craftedcore.network.NetworkManager;
import tocraft.walkers.Walkers;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.NetworkHandler;
import tocraft.ycdm.impl.PAPlayerDataProvider;

public class PlayerAbilities {

    /**
     * Returns an integer representing the current ability cooldown of the specified {@link Player} in ticks.
     *
     * <p>
     * A return value of {@code 0} represents no cooldown, while 20 is 1 second.
     *
     * @param player player to retrieve ability cooldown for
     * @return cooldown, in ticks, of the specified player's ability
     */
    public static int getCooldown(Player player) {
        return ((PlayerDataProvider) player).getAbilityCooldown();
    }

    public static boolean canUseAbility(Player player) {
    	// return false in case the player's already in cooldown as of ycdm
    	if (Walkers.foundPotionAbilities && ((PAPlayerDataProvider) player).getCooldown() <= 0)
    		return false;
    	else
    		return ((PlayerDataProvider) player).getAbilityCooldown() <= 0;
    }

    public static void setCooldown(Player player, int cooldown) {
        ((PlayerDataProvider) player).setAbilityCooldown(cooldown);
    }

    public static void sync(ServerPlayer player) {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeInt(((PlayerDataProvider) player).getAbilityCooldown());
        NetworkManager.sendToPlayer(player, NetworkHandler.ABILITY_SYNC, packet);
    }
}
