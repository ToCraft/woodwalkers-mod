package tocraft.walkers.eventhandler;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tocraft.craftedcore.event.common.EntityEvents;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.AquaticSkill;
import tocraft.walkers.skills.impl.UndrownableSkill;

public final class LivingBreatheHandler implements EntityEvents.LivingBreathe {
    @Override
    public boolean breathe(LivingEntity entity, boolean canBreathe) {
        if (entity instanceof Player) {
            LivingEntity shape = PlayerShape.getCurrentShape((Player) entity);

            if (shape != null) {
                boolean isInWater = entity.isInWaterOrBubble();

                for (AquaticSkill<LivingEntity> skill : SkillRegistry.get(shape, AquaticSkill.ID).stream().map(skill -> (AquaticSkill<LivingEntity>) skill).toList()) {
                    return isInWater ? skill.isAquatic : skill.isLand;
                }

                if (SkillRegistry.has(shape, UndrownableSkill.ID) || shape.canBreatheUnderwater() && isInWater) {
                    return true;
                }

            }
        }
        return canBreathe;
    }
}
