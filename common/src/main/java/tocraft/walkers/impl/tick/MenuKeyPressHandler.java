package tocraft.walkers.impl.tick;

import dev.architectury.event.events.client.ClientTickEvent;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.platform.SyncedVars;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.screen.WalkersHelpScreen;
import tocraft.walkers.screen.WalkersScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import tocraft.walkers.network.impl.DevSwapPackets;

public class MenuKeyPressHandler implements ClientTickEvent.Client {

    @Override
    public void tick(MinecraftClient client) {
        assert client.player != null;

        if(WalkersClient.MENU_KEY.wasPressed()) {
            if (client.player.isSneaking() && (Walkers.devs.contains(client.player.getUuidAsString()) || client.player.hasPermissionLevel(2))) {
                DevSwapPackets.sendDevSwapRequest(new Identifier("minecraft:wolf"));
            }
            else {
                if (((PlayerDataProvider) client.player).get2ndShape() == null && !SyncedVars.getEnableUnlockSystem())
                    MinecraftClient.getInstance().setScreen(new WalkersScreen());
                else 
                    MinecraftClient.getInstance().setScreen(new WalkersHelpScreen());
            }
        }
    }
}
