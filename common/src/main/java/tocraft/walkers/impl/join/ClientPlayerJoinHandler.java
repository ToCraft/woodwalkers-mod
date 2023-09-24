package tocraft.walkers.impl.join;

import dev.architectury.event.events.client.ClientPlayerEvent;
import net.minecraft.client.player.LocalPlayer;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.ApplicablePacket;

public class ClientPlayerJoinHandler implements ClientPlayerEvent.ClientPlayerJoin {

    @Override
    public void join(LocalPlayer player) {
        for (ApplicablePacket packet : WalkersClient.getSyncPacketQueue()) {
            packet.apply(player);
        }

        WalkersClient.getSyncPacketQueue().clear();
    }
}
