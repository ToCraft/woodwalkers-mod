package tocraft.walkers.fabric;

import net.fabricmc.api.ModInitializer;
import tocraft.walkers.Walkers;

public class WalkersFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        new Walkers().initialize();
    }
}
