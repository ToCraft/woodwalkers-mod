package dev.tocraft.walkers.eventhandler;

import dev.tocraft.craftedcore.event.common.PlayerEvents;
import dev.tocraft.walkers.api.PlayerShapeChanger;
import dev.tocraft.walkers.impl.PlayerDataProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import static dev.tocraft.walkers.Walkers.WAYPOINT_TRANSMIT_MODIFIER;

public final class RespawnHandler implements PlayerEvents.PlayerRespawn {
    @Override
    public void clone(ServerPlayer oldPlayer, @NotNull ServerPlayer newPlayer) {
        ((PlayerDataProvider) newPlayer).walkers$setCurrentShape(((PlayerDataProvider) oldPlayer).walkers$getCurrentShape());
        ((PlayerDataProvider) newPlayer).walkers$set2ndShape(((PlayerDataProvider) oldPlayer).walkers$get2ndShape());
        ((PlayerDataProvider) newPlayer).walkers$setAbilityCooldown(((PlayerDataProvider) oldPlayer).walkers$getAbilityCooldown());
        ((PlayerDataProvider) newPlayer).walkers$setRemainingHostilityTime(((PlayerDataProvider) oldPlayer).walkers$getRemainingHostilityTime());

        newPlayer.refreshDimensions();

        // disable waypoint to show on locator bar
        AttributeInstance waypointTransmitRange = newPlayer.getAttribute(Attributes.WAYPOINT_TRANSMIT_RANGE);
        if (waypointTransmitRange != null) {
            waypointTransmitRange.addOrReplacePermanentModifier(WAYPOINT_TRANSMIT_MODIFIER);
        }

        PlayerShapeChanger.sync(newPlayer);
    }
}
