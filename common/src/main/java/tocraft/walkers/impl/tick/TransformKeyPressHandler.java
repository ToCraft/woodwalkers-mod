package tocraft.walkers.impl.tick;

import dev.architectury.event.events.client.ClientTickEvent;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.impl.SwapPackets;
import net.minecraft.client.MinecraftClient;

public class TransformKeyPressHandler implements ClientTickEvent.Client {
    @Override
    public void tick(MinecraftClient client) {
        assert client.player != null;

        if(WalkersClient.TRANSFORM_KEY.wasPressed()) {
            if (PlayerShape.getCurrentShape(client.player) == null)
                SwapPackets.sendSwapRequest(((PlayerDataProvider) client.player).get2ndShape(), false);
            else
                SwapPackets.sendSwapRequest(null, false);
        }
    }
}
