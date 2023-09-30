package tocraft.walkers.forge;

import dev.architectury.platform.Platform;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import tocraft.walkers.Walkers;

@Mod(Walkers.MODID)
public class WalkersForge {

	public WalkersForge() {
		getModVersion();
		new Walkers().initialize();

		if (Platform.getEnv().isClient()) {
			new WalkersForgeClient();
		}
	}

	public void getModVersion() {
		ModContainer modContainer = ModList.get().getModContainerById(Walkers.MODID).get();
		Walkers.setVersion(modContainer.getModInfo().getVersion().toString());
	}
}
