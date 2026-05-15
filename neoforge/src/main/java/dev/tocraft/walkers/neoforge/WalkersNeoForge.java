package dev.tocraft.walkers.neoforge;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.WalkersClient;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@SuppressWarnings("unused")
@Mod(Walkers.MODID)
public class WalkersNeoForge {
    public WalkersNeoForge() {
        new Walkers().initialize();

        if (FMLEnvironment.getDist().isClient()) {
            new WalkersClient().initialize();
        }
    }
}
