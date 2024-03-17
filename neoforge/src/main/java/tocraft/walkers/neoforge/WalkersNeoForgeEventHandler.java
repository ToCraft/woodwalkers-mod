package tocraft.walkers.neoforge;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Walkers.MODID)
public class WalkersNeoForgeEventHandler {

    @SubscribeEvent
    public static void livingBreath(LivingBreatheEvent event) {
        if (event.getEntity() instanceof Player) {
            LivingEntity shape = PlayerShape.getCurrentShape((Player) event.getEntity());

            if (shape != null) {
                if (Walkers.isAquatic(shape) < 1) {
                    event.setCanBreathe(false);
                }
            }
        }
    }
}
