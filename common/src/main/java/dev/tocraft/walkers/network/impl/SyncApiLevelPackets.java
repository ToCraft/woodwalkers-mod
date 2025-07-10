package dev.tocraft.walkers.network.impl;

import dev.tocraft.craftedcore.network.ModernNetworking;
import dev.tocraft.walkers.api.platform.ApiLevel;
import dev.tocraft.walkers.network.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class SyncApiLevelPackets {
    public static void handleSyncPacket(CompoundTag nbt) {
        if (nbt != null) {
            String apiLevelId = nbt.getString("api_level").orElseThrow();
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
