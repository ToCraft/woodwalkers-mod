package tocraft.walkers.fabric;

import tocraft.walkers.Walkers;
import tocraft.walkers.fabric.config.WalkersFabricConfig;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class WalkersFabric implements ModInitializer {

    public static final WalkersFabricConfig CONFIG = OmegaConfig.register(WalkersFabricConfig.class);

    @Override
    public void onInitialize() {
        getModVersion();
        new Walkers().initialize();
    }

    public void getModVersion() {
        ModContainer modContainer = FabricLoader.getInstance().getModContainer("walkers").get();
        Walkers.setVersion(modContainer.getMetadata().getVersion().getFriendlyString());
    }
}
