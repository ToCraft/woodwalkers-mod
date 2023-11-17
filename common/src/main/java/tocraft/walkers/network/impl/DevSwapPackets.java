package tocraft.walkers.network.impl;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.network.NetworkManager;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;

public class DevSwapPackets {

	public static void registerDevRequestPacketHandler() {
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.DEV_SHAPE_REQUEST, (buf, context) -> {
			ResourceLocation id = buf.readResourceLocation();

			context.getPlayer().getServer().execute(() -> {
				CompoundTag nbt = new CompoundTag();
				nbt.putBoolean("isDev", true);
				nbt.putString("id", id.toString());
				ServerLevel serverWorld = ((ServerPlayer) context.getPlayer()).serverLevel();
				Entity created = EntityType.loadEntityRecursive(nbt, serverWorld, it -> it);

				if (created instanceof LivingEntity living) {
					@Nullable
					ShapeType<?> defaultType = ShapeType.from(living);

					if (defaultType != null) {
						if (((PlayerDataProvider) context.getPlayer()).walkers$get2ndShape() != null
								&& defaultType.getEntityType() == ((PlayerDataProvider) context.getPlayer())
										.walkers$get2ndShape().getEntityType())
							PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), defaultType,
									(LivingEntity) created);
						else if (Walkers.CONFIG.devShapeIsThirdShape)
							PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), defaultType,
									(LivingEntity) created);
					}
				}

				// Refresh player dimensions
				context.getPlayer().refreshDimensions();
			});
		});
	}

	public static void sendDevSwapRequest(ResourceLocation id) {
		FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());

		packet.writeResourceLocation(id);

		NetworkManager.sendToServer(ClientNetworking.DEV_SHAPE_REQUEST, packet);
	}
}
