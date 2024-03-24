package tocraft.walkers.network.impl;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;

public class SwapVariantPackets {

    @SuppressWarnings("ConstantConditions")
    public static void registerSwapVariantPacketHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.VARIANT_REQUEST,
                (buf, context) -> context.getPlayer().getServer().execute(() -> {
                    if (Walkers.CONFIG.unlockEveryVariant) {
                        int variantID = buf.readInt();
                        ShapeType<?> currentShapeType = ShapeType.from(PlayerShape.getCurrentShape(context.getPlayer()));
                        if (currentShapeType != null && currentShapeType.getVariantData() != variantID) {
                            ShapeType<?> newShapeType = ShapeType.from(currentShapeType.getEntityType(), variantID);
                            if (newShapeType != null) {
                                if (PlayerShapeChanger.change2ndShape((ServerPlayer) context.getPlayer(), newShapeType)) {
                                    LivingEntity shape = newShapeType.create(context.getPlayer().level(), context.getPlayer());
                                    if (shape != null) {
                                        PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), shape);
                                    }
                                }
                            }
                        }
                    }
                }));
    }

    public static void sendSwapRequest(int variantID) {
        if (Walkers.CONFIG.unlockEveryVariant) {
            FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
            packet.writeInt(variantID);
            NetworkManager.sendToServer(ClientNetworking.VARIANT_REQUEST, packet);
        }
    }
}
