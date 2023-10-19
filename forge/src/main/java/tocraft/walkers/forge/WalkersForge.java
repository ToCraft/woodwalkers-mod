package tocraft.walkers.forge;

import net.minecraftforge.fml.common.Mod;
import tocraft.craftedcore.platform.Platform;
import tocraft.walkers.Walkers;

@Mod(Walkers.MODID)
public class WalkersForge {

	public WalkersForge() {
		new Walkers().initialize();

		if (Platform.getDist().isClient()) {
			new WalkersForgeClient();
		}
	}
}
