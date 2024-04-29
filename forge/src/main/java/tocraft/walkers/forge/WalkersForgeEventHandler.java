package tocraft.walkers.forge;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.NocturnalSkill;

@SuppressWarnings("unused")
public class WalkersForgeEventHandler {
    @SubscribeEvent
    public void sleepFinishedTime(SleepFinishedTimeEvent event) {
        if (event.getWorld() instanceof Level && ((Level) event.getWorld()).isDay()) {
            if (event.getWorld().players().stream().anyMatch(player -> SkillRegistry.has(PlayerShape.getCurrentShape(player), NocturnalSkill.ID))) {
                event.setTimeAddition(event.getNewTime() + ((ServerLevel) event.getWorld()).getDayTime() % 24000L > 12000L ? 13000 : -11000);
            }
        }
    }

    @SubscribeEvent
    public void sleepTimeCheck(SleepingTimeCheckEvent event) {
        if (SkillRegistry.has(PlayerShape.getCurrentShape((Player) event.getEntity()), NocturnalSkill.ID)) {
            event.setResult(event.getEntity().level.isDay() ? Event.Result.ALLOW : Event.Result.DENY);
        }
    }
}
