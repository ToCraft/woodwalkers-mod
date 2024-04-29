package tocraft.walkers.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import tocraft.walkers.Walkers;

@SuppressWarnings("unused")
@Mod(Walkers.MODID)
public class WalkersForge {

    public WalkersForge() {
        new Walkers().initialize();

        MinecraftForge.EVENT_BUS.register(new WalkersForgeEventHandler());

        if (FMLEnvironment.dist.isClient())
            new WalkersForgeClient();
    }
}
