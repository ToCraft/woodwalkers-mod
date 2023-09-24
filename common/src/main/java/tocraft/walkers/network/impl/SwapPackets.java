package tocraft.walkers.network.impl;

import dev.architectury.networking.NetworkManager;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.platform.SyncedVars;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class SwapPackets {

    public static void registerWalkersRequestPacketHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.SHAPE_REQUEST, (buf, context) -> {
            boolean validType = buf.readBoolean();
            if(validType) {
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(buf.readResourceLocation());
                int variant = buf.readInt();
                boolean unlock = buf.readBoolean();

                context.getPlayer().getServer().execute(() -> {
                    // player type shouldn't be sent, but we still check regardless
                    if(entityType.equals(EntityType.PLAYER)) {
                        PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), null, null);
                    } else {
                        @Nullable ShapeType<LivingEntity> type = ShapeType.from(entityType, variant);
                        if(type != null) {
                            // unlock walker, if requested
                            if (unlock) {
                                // tests, if unlocking is possible
                                if (SyncedVars.getUnlockOveridesCurrentShape() || ((PlayerDataProvider)context.getPlayer()).get2ndShape() == null)
                                    // Ensures the mob isn't blacklisted
                                    if (!SyncedVars.getShapeBlacklist().contains(EntityType.getKey(type.getEntityType()).toString())) {
                                        // set 2nd shape
                                        PlayerShapeChanger.change2ndShape((ServerPlayer) context.getPlayer(), type);
                                        // update Player
                                        PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), type, type.create(context.getPlayer().level()));
                                    }
                            }
                            else {
                                // update Player
                                PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), type, type.create(context.getPlayer().level()));
                            }
                        }
                    }
                    
                    // Refresh player dimensions
                    context.getPlayer().refreshDimensions();
                });
            } else {
                // Swap back to player if server allows it
                context.getPlayer().getServer().execute(() -> {
                    PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), null, null);

                    context.getPlayer().refreshDimensions();
                });
            }
        });
    }

    public static void sendSwapRequest(@Nullable ShapeType<?> type, boolean unlock) {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());

        packet.writeBoolean(type != null);
        if(type != null) {
            packet.writeResourceLocation(BuiltInRegistries.ENTITY_TYPE.getKey(type.getEntityType()));
            packet.writeInt(type.getVariantData());
            packet.writeBoolean(unlock);
        }

        NetworkManager.sendToServer(ClientNetworking.SHAPE_REQUEST, packet);
    }
}
