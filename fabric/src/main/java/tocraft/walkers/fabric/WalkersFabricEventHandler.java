package tocraft.walkers.fabric;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.world.InteractionResult;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.NocturnalSkill;

public class WalkersFabricEventHandler {
    public void initialize() {
        // NocturnalSkill
        EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanillaResult) -> {
            if (SkillRegistry.has(PlayerShape.getCurrentShape(player), NocturnalSkill.ID)) {
                return player.level().isDay() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
    }
}
