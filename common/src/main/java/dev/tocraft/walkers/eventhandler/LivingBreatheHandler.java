package dev.tocraft.walkers.eventhandler;

import dev.tocraft.craftedcore.event.common.EntityEvents;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.traits.TraitRegistry;
import dev.tocraft.walkers.traits.impl.AquaticTrait;
import dev.tocraft.walkers.traits.impl.UndrownableTrait;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

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
