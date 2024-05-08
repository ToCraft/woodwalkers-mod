package tocraft.walkers.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import tocraft.walkers.api.events.ShapeEvents;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.impl.UnlockPackets;

public class PlayerShapeChanger {

    public static boolean change2ndShape(ServerPlayer player, ShapeType<?> newShape) {
        PlayerDataProvider provider = (PlayerDataProvider) player;
        InteractionResult unlock = ShapeEvents.UNLOCK_SHAPE.invoke().unlock(player, newShape);

        if (unlock != InteractionResult.FAIL) {
            provider.walkers$set2ndShape(newShape);
            sync(player);
            PlayerAbilities.sync(player);
            return true;
        } else {
            return false;
        }
    }

    public static void sync(ServerPlayer player) {
        UnlockPackets.sendSyncPacket(player);
    }
}
