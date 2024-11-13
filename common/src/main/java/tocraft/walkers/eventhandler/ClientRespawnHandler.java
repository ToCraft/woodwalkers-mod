package tocraft.walkers.eventhandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import tocraft.craftedcore.event.client.ClientPlayerEvents;
import tocraft.walkers.impl.DimensionsRefresher;
import tocraft.walkers.impl.PlayerDataProvider;

@Environment(EnvType.CLIENT)
public final class ClientRespawnHandler implements ClientPlayerEvents.ClientPlayerRespawn {
    @Override
    public void respawn(LocalPlayer oldPlayer, LocalPlayer newPlayer) {
        ((PlayerDataProvider) newPlayer).walkers$setCurrentShape(((PlayerDataProvider) oldPlayer).walkers$getCurrentShape());
        ((PlayerDataProvider) newPlayer).walkers$set2ndShape(((PlayerDataProvider) oldPlayer).walkers$get2ndShape());
        ((PlayerDataProvider) newPlayer).walkers$setAbilityCooldown(((PlayerDataProvider) oldPlayer).walkers$getAbilityCooldown());
        ((PlayerDataProvider) newPlayer).walkers$setRemainingHostilityTime(((PlayerDataProvider) oldPlayer).walkers$getRemainingHostilityTime());

        ((DimensionsRefresher) newPlayer).shape_refreshDimensions();
    }
}
