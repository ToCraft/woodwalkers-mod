package dev.tocraft.walkers.api;

import dev.tocraft.walkers.api.events.ShapeEvents;
import dev.tocraft.walkers.api.variant.ShapeType;
import dev.tocraft.walkers.impl.PlayerDataProvider;
import dev.tocraft.walkers.network.impl.UnlockPackets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.ApiStatus;

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

    @ApiStatus.Internal
    public static void sync(ServerPlayer player) {
        UnlockPackets.sendSyncPacket(player);
    }
}
