package tocraft.walkers.impl.join;

import net.minecraft.client.player.LocalPlayer;
import tocraft.craftedcore.events.client.ClientPlayerEvents;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.ApplicablePacket;

public class ClientPlayerJoinHandler implements ClientPlayerEvents.ClientPlayerJoin {

    @Override
    public void join(LocalPlayer player) {
        for (ApplicablePacket packet : WalkersClient.getSyncPacketQueue()) {
            packet.apply(player);
        }

        WalkersClient.getSyncPacketQueue().clear();
    }
}
