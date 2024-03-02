package tocraft.walkers.api;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.NetworkHandler;

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
        return ((PlayerDataProvider) player).walkers$getAbilityCooldown();
    }

    public static boolean canUseAbility(Player player) {
        return !player.isSpectator() && ((PlayerDataProvider) player).walkers$getAbilityCooldown() <= 0;
    }

    public static void setCooldown(Player player, int cooldown) {
        ((PlayerDataProvider) player).walkers$setAbilityCooldown(cooldown);
    }

    public static void sync(ServerPlayer player) {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
        packet.writeInt(((PlayerDataProvider) player).walkers$getAbilityCooldown());
        NetworkManager.sendToPlayer(player, NetworkHandler.ABILITY_SYNC, packet);
    }
}
