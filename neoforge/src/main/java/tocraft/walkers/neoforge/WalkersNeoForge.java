package tocraft.walkers.neoforge;

import net.neoforged.fml.common.Mod;
import tocraft.craftedcore.platform.Platform;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;

@Mod(Walkers.MODID)
public class WalkersNeoForge {
	public WalkersNeoForge() {
		new Walkers().initialize();

		if (Platform.getDist().isClient()) {
			new WalkersClient().initialize();
		}
	}
}
