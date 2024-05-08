package tocraft.walkers.forge;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.command.WalkersCommand;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Walkers.MODID)
public class WalkersForgeModEventHandler {

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

    @SubscribeEvent
    public static void event(RegisterCommandsEvent event) {
        WalkersCommand.register(event.getDispatcher(), event.getBuildContext());
    }
}
