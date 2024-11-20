package tocraft.walkers.eventhandler;

import net.minecraft.server.level.ServerPlayer;
import tocraft.craftedcore.event.common.PlayerEvents;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.impl.PlayerDataProvider;

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
