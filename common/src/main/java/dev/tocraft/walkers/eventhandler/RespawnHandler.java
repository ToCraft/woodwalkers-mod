package dev.tocraft.walkers.eventhandler;

import dev.tocraft.craftedcore.event.common.PlayerEvents;
import dev.tocraft.walkers.api.PlayerShapeChanger;
import dev.tocraft.walkers.impl.PlayerDataProvider;
import net.minecraft.server.level.ServerPlayer;

public final class RespawnHandler implements PlayerEvents.PlayerRespawn {
    @Override
    public void clone(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
        ((PlayerDataProvider) newPlayer).walkers$setCurrentShape(((PlayerDataProvider) oldPlayer).walkers$getCurrentShape());
        ((PlayerDataProvider) newPlayer).walkers$set2ndShape(((PlayerDataProvider) oldPlayer).walkers$get2ndShape());
        ((PlayerDataProvider) newPlayer).walkers$setAbilityCooldown(((PlayerDataProvider) oldPlayer).walkers$getAbilityCooldown());
        ((PlayerDataProvider) newPlayer).walkers$setRemainingHostilityTime(((PlayerDataProvider) oldPlayer).walkers$getRemainingHostilityTime());

        newPlayer.refreshDimensions();

        PlayerShapeChanger.sync(newPlayer);
    }
}
