package tocraft.walkers.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import tocraft.craftedcore.events.Event;
import tocraft.walkers.api.event.ShapeEvents;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.impl.UnlockPackets;

public class PlayerShapeChanger {

    public static boolean change2ndShape(ServerPlayer player, ShapeType newShape) {
        PlayerDataProvider provider = (PlayerDataProvider) player;
        Event.Result unlock = ShapeEvents.UNLOCK_SHAPE.invoker().unlock(player, newShape);

        if(unlock.asMinecraft() != InteractionResult.FAIL && provider.get2ndShape() != newShape) {
            provider.set2ndShape(newShape);
            sync(player);
            PlayerAbilities.sync(player); // TODO: ???
            return true;
        } else {
            return false;
        }
    }

    public static void sync(ServerPlayer player) {
        UnlockPackets.sendSyncPacket(player);
    }
}
