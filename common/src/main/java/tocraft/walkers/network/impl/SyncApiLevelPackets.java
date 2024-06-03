package tocraft.walkers.network.impl;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import tocraft.craftedcore.network.ModernNetworking;
import tocraft.walkers.api.platform.ApiLevel;
import tocraft.walkers.network.NetworkHandler;

public class SyncApiLevelPackets {
    public static void handleSyncPacket(CompoundTag nbt) {
        if (nbt != null) {
            String apiLevelId = nbt.getString("api_level");
            ApiLevel apiLevel = ApiLevel.valueOf(apiLevelId);
            ApiLevel.ON_API_LEVEL_CHANGE_EVENT.invoke().setServerApiLevel(apiLevel);
        }
    }

    public static void sendSyncPacket(ServerPlayer player) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("api_level", ApiLevel.getCurrentLevel().name());

        // Send to client
        ModernNetworking.sendToPlayer(player, NetworkHandler.SYNC_API_LEVEL, nbt);
    }
}
