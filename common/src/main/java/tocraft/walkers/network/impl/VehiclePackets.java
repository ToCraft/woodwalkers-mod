package tocraft.walkers.network.impl;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import tocraft.craftedcore.network.ModernNetworking;
import tocraft.craftedcore.patched.CEntity;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.mixin.EntityTrackerAccessor;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;
import tocraft.walkers.network.NetworkHandler;

import java.util.UUID;

public class VehiclePackets {
    public static void handleSyncPacket(ModernNetworking.Context context, CompoundTag tag) {
        if (context.getPlayer() != null && tag != null) {
            UUID sender = tag.getUUID("senderID");
            Player commandSender = context.getPlayer().getCommandSenderWorld().getPlayerByUUID(sender);
            if (commandSender != null) {
                UUID playerVehicleID = tag.getBoolean("isRidingPlayer") ? tag.getUUID("playerVehicleID") : null;

                ((PlayerDataProvider) commandSender).walkers$setVehiclePlayerUUID(playerVehicleID);
            }
        }
    }

    public static void sync(ServerPlayer player) {
        CompoundTag tag = new CompoundTag();
        boolean isRidingPlayer = player.getVehicle() instanceof ServerPlayer;
        tag.putUUID("senderID", player.getUUID());
        tag.putBoolean("isRidingPlayer", isRidingPlayer);

        if (isRidingPlayer) tag.putUUID("playerVehicleID", player.getVehicle().getUUID());

        Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) ((ServerLevel) CEntity.level(player)).getChunkSource().chunkMap).getEntityMap();
        Object tracking = trackers.get(player.getId());
        // Send to all clients
        if (tracking != null)
            ((EntityTrackerAccessor) tracking).getSeenBy().forEach(listener -> {
                ModernNetworking.sendToPlayer(listener.getPlayer(), NetworkHandler.CHANGE_VEHICLE_STATE, tag.copy());
            });
    }
}
