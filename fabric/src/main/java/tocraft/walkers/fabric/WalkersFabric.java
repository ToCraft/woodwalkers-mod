package tocraft.walkers.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import tocraft.walkers.Walkers;

public class WalkersFabric implements ModInitializer {

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
