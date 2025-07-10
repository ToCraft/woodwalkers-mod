package tocraft.walkers.eventhandler;

import dev.tocraft.craftedcore.event.common.EntityEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.AquaticTrait;
import tocraft.walkers.traits.impl.UndrownableTrait;

public final class LivingBreatheHandler implements EntityEvents.LivingBreathe {
    @Override
    public boolean breathe(LivingEntity entity, boolean canBreathe) {
        if (entity instanceof Player) {
            LivingEntity shape = PlayerShape.getCurrentShape((Player) entity);

            if (shape != null) {
                boolean isInWater = entity.isInWater();

                for (AquaticTrait<LivingEntity> trait : TraitRegistry.get(shape, AquaticTrait.ID).stream().map(trait -> (AquaticTrait<LivingEntity>) trait).toList()) {
                    return isInWater ? trait.isAquatic : trait.isLand;
                }

                if (TraitRegistry.has(shape, UndrownableTrait.ID) || shape.canBreatheUnderwater() && isInWater) {
                    return true;
                }

            }
        }
        return canBreathe;
    }
}
