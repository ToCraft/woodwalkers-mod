package tocraft.walkers.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import tocraft.craftedcore.event.common.PlayerEvents;
import tocraft.craftedcore.network.ModernNetworking;
import tocraft.walkers.api.PlayerAbilities;
import tocraft.walkers.network.impl.SwapPackets;
import tocraft.walkers.network.impl.SwapVariantPackets;
import tocraft.walkers.network.impl.SyncApiLevelPackets;
import tocraft.walkers.network.impl.UnlockPackets;

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

        PlayerEvents.PLAYER_JOIN.register(SyncApiLevelPackets::sendSyncPacket);
    }

    @SuppressWarnings("ConstantConditions")
    private static void registerUseAbilityPacketHandler() {
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, USE_ABILITY, (context, packet) -> {
            Player player = context.getPlayer();

            context.getPlayer().getServer().execute(() -> PlayerAbilities.useAbility((ServerPlayer) player));
        });
    }
}
