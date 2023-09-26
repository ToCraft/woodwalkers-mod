package tocraft.walkers.network.impl;

import org.jetbrains.annotations.Nullable;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;

public class SwapPackets {

	public static void registerWalkersRequestPacketHandler() {
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.SHAPE_REQUEST, (buf, context) -> {
			boolean validType = buf.readBoolean();
			if (validType) {
				EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(buf.readResourceLocation());
				int variant = buf.readInt();

				context.getPlayer().getServer().execute(() -> {
					// player type shouldn't be sent, but we still check regardless
					if (entityType.equals(EntityType.PLAYER)) {
						PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), null, null);
					} else {
						@Nullable
						ShapeType<LivingEntity> type = ShapeType.from(entityType, variant);
						if (type != null) {
							// update Player
							PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), type,
									type.create(context.getPlayer().level()));
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

	public static void sendSwapRequest(@Nullable ShapeType<?> type) {
		FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());

		packet.writeBoolean(type != null);
		if (type != null) {
			packet.writeResourceLocation(BuiltInRegistries.ENTITY_TYPE.getKey(type.getEntityType()));
			packet.writeInt(type.getVariantData());
		}

		NetworkManager.sendToServer(ClientNetworking.SHAPE_REQUEST, packet);
	}
}
