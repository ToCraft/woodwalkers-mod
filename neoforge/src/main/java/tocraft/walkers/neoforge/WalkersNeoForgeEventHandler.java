package tocraft.walkers.neoforge;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.SleepingTimeCheckEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.NocturnalSkill;

@SuppressWarnings("unused")
public class WalkersNeoForgeEventHandler {
    @SubscribeEvent
    public void sleepFinishedTime(SleepFinishedTimeEvent event) {
        if (event.getLevel() instanceof Level && ((Level) event.getLevel()).isDay()) {
            if (event.getLevel().players().stream().anyMatch(player -> SkillRegistry.has(PlayerShape.getCurrentShape(player), NocturnalSkill.ID))) {
                event.setTimeAddition(event.getNewTime() + ((ServerLevel) event.getLevel()).getDayTime() % 24000L > 12000L ? 13000 : -11000);
            }
        }
    }

    @SubscribeEvent
    public void sleepTimeCheck(SleepingTimeCheckEvent event) {
        if (SkillRegistry.has(PlayerShape.getCurrentShape(event.getEntity()), NocturnalSkill.ID)) {
            event.setResult(event.getEntity().level().isDay() ? Event.Result.ALLOW : Event.Result.DENY);
        }
    }
}
