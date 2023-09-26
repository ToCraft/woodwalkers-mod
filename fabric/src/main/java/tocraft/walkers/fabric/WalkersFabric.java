package tocraft.walkers.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import tocraft.walkers.Walkers;
import tocraft.walkers.fabric.config.ConfigLoader;
import tocraft.walkers.fabric.config.WalkersFabricConfig;

public class WalkersFabric implements ModInitializer {

	public static final WalkersFabricConfig CONFIG = ConfigLoader.read();

	@Override
	public void onInitialize() {
		getModVersion();
		new Walkers().initialize();
	}

	public void getModVersion() {
		ModContainer modContainer = FabricLoader.getInstance().getModContainer(Walkers.MODID).get();
		Walkers.setVersion(modContainer.getMetadata().getVersion().getFriendlyString());
	}
}
