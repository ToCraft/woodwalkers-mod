package tocraft.walkers.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import tocraft.walkers.WalkersClient;

@Environment(EnvType.CLIENT)
public class WalkersFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        new WalkersClient().initialize();
    }
}
