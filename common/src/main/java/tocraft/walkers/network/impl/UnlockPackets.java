package tocraft.walkers.network.impl;

import io.netty.buffer.Unpooled;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.network.NetworkManager;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;
import tocraft.walkers.registry.WalkersEntityTags;

public class UnlockPackets {

	private static final String UNLOCK_KEY = "UnlockedShape";

	public static void handleUnlockSyncPacket(FriendlyByteBuf packet, NetworkManager.PacketContext context) {
		CompoundTag nbt = packet.readNbt();
		if (nbt != null) {
			CompoundTag idTag = nbt.getCompound(UNLOCK_KEY);

			ClientNetworking.runOrQueue(context, player -> {
				if (idTag != null)
					((PlayerDataProvider) player).walkers$set2ndShape(ShapeType.from(idTag));
			});
		}
	}

	/**
	 * Server handles request, that 2nd shape may be changed
	 */
	public static void registerShapeUnlockRequestPacketHandler() {
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.UNLOCK_REQUEST, (buf, context) -> {
			// check if player is blacklisted
			if (Walkers.CONFIG.playerUUIDBlacklist.contains(context.getPlayer().getUUID()))
				return;

			boolean validType = buf.readBoolean();
			if (validType) {
				EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(buf.readResourceLocation());
				int variant = buf.readInt();

				context.getPlayer().getServer().execute(() -> {
					@Nullable
					ShapeType<LivingEntity> type = ShapeType.from(entityType, variant);
					if (type != null && !type.getEntityType().is(WalkersEntityTags.BLACKLISTED) && (Walkers.CONFIG.unlockOveridesCurrentShape || ((PlayerDataProvider) context.getPlayer()).walkers$get2ndShape() == null)) {
						// set 2nd shape
						boolean result = PlayerShapeChanger.change2ndShape((ServerPlayer) context.getPlayer(), type);
						// update Player
						if (result)
							PlayerShape.updateShapes((ServerPlayer) context.getPlayer(),
								type.create(context.getPlayer().level()));
					}

					// Refresh player dimensions
					context.getPlayer().refreshDimensions();
				});
			} else {
				// Swap back to player if server allows it
				context.getPlayer().getServer().execute(() -> {
					PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), null);

					context.getPlayer().refreshDimensions();
				});
			}
		});
	}

	/**
	 * Server synchronizes unlocked shape with the client
	 * 
	 */
	public static void sendSyncPacket(ServerPlayer player) {
		FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());

		// Serialize unlocked to tag
		CompoundTag compound = new CompoundTag();
		CompoundTag id = new CompoundTag();
		if (((PlayerDataProvider) player).walkers$get2ndShape() != null)
			id = ((PlayerDataProvider) player).walkers$get2ndShape().writeCompound();
		compound.put(UNLOCK_KEY, id);
		packet.writeNbt(compound);

		// Send to client
		NetworkManager.sendToPlayer(player, NetworkHandler.UNLOCK_SYNC, packet);
	}

	/**
	 * Client requests, that server may unlock a shape
	 * 
	 */
	public static void sendUnlockRequest(@Nullable ShapeType<?> type) {
		FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());

		packet.writeBoolean(type != null);
		if (type != null) {
			packet.writeResourceLocation(BuiltInRegistries.ENTITY_TYPE.getKey(type.getEntityType()));
			packet.writeInt(type.getVariantData());
		}

		NetworkManager.sendToServer(ClientNetworking.UNLOCK_REQUEST, packet);
	}
}
