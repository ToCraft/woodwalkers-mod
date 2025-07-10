package tocraft.walkers.network.impl;

import dev.tocraft.craftedcore.network.ModernNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.platform.ApiLevel;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;

public class SwapPackets {

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static void registerWalkersRequestPacketHandler() {
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, NetworkHandler.SHAPE_REQUEST,
                (context, packet) -> context.getPlayer().getServer().execute(() -> {
                    // check if player is blacklisted
                    if (Walkers.isPlayerBlacklisted(context.getPlayer().getUUID()) && Walkers.CONFIG.blacklistPreventsMorphing) {
                        context.getPlayer().displayClientMessage(Component.translatable("walkers.player_blacklisted"), true);
                        return;
                    }

                    if (!ApiLevel.getCurrentLevel().canMorph) {
                        return;
                    }

                    // make the default ShapeType null, doing it this way, it's ensured that invalid 2ndShapes won't cause crashes.
                    @Nullable
                    ShapeType<LivingEntity> type = null;
                    // Get 2ndShape of Player, if the Player is in first shape rn.
                    if (PlayerShape.getCurrentShape(context.getPlayer()) == null) {
                        type = (@Nullable ShapeType<LivingEntity>) ((PlayerDataProvider) context.getPlayer()).walkers$get2ndShape();
                    }

                    // Swap to other Shape
                    if (type != null) {
                        // update Player
                        PlayerShape.updateShapes((ServerPlayer) context.getPlayer(),
                                type.create(context.getPlayer().level(), context.getPlayer()));
                    } else {
                        // Swap back to player if server allows it
                        PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), null);
                    }

                    // Refresh player dimensions
                    context.getPlayer().refreshDimensions();
                }));
    }

    public static void sendSwapRequest() {
        if (!ApiLevel.getCurrentLevel().canMorph) {
            return;
        }

        ModernNetworking.sendToServer(ClientNetworking.SHAPE_REQUEST, new CompoundTag());
    }
}
