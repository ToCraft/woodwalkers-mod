package dev.tocraft.walkers.network;

import dev.tocraft.craftedcore.event.common.PlayerEvents;
import dev.tocraft.craftedcore.network.ModernNetworking;
import dev.tocraft.walkers.api.PlayerAbilities;
import dev.tocraft.walkers.network.impl.SwapPackets;
import dev.tocraft.walkers.network.impl.SwapVariantPackets;
import dev.tocraft.walkers.network.impl.SyncApiLevelPackets;
import dev.tocraft.walkers.network.impl.UnlockPackets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class ServerNetworking implements NetworkHandler {

    public static void initialize() {
        SwapPackets.registerWalkersRequestPacketHandler();
        UnlockPackets.registerShapeUnlockRequestPacketHandler();
        SwapVariantPackets.registerSwapVariantPacketHandler();
        registerUseAbilityPacketHandler();

        ModernNetworking.registerType(SHAPE_SYNC);
        ModernNetworking.registerType(ABILITY_SYNC);
        ModernNetworking.registerType(UNLOCK_SYNC);
        ModernNetworking.registerType(SYNC_API_LEVEL);

        // sync API level and 2nd Shape
        PlayerEvents.PLAYER_JOIN.register(player -> {
            SyncApiLevelPackets.sendSyncPacket(player);
            UnlockPackets.sendSyncPacket(player);
        });
    }

    @SuppressWarnings("ConstantConditions")
    private static void registerUseAbilityPacketHandler() {
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, USE_ABILITY, (context, packet) -> {
            Player player = context.getPlayer();

            context.getPlayer().getServer().execute(() -> PlayerAbilities.useAbility((ServerPlayer) player));
        });
    }
}
