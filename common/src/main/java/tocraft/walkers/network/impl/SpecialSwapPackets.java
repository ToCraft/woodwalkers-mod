package tocraft.walkers.network.impl;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;

public class SpecialSwapPackets {

    public static void registerDevRequestPacketHandler() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, NetworkHandler.SPECIAL_SHAPE_REQUEST, (buf, context) -> {

            context.getPlayer().getServer().execute(() -> {
                ServerPlayer player = (ServerPlayer) context.getPlayer();
                ResourceLocation shape = new ResourceLocation("minecraft:wolf");

                // check if player has a special shape
                if (!Walkers.hasSpecialShape(player.getUUID()) || (!Walkers.CONFIG.specialShapeIsThirdShape && (((PlayerDataProvider) player).walkers$get2ndShape() == null || !((PlayerDataProvider) player).walkers$get2ndShape().getEntityType().equals(EntityType.WOLF))))
                    return;

                Entity created;
                CompoundTag nbt = new CompoundTag();

                nbt.putBoolean("isSpecial", true);
                nbt.putString("id", shape.toString());
                ServerLevel serverWorld = player.getLevel();
                created = EntityType.loadEntityRecursive(nbt, serverWorld, it -> it);

                if (created instanceof LivingEntity living) {
                    PlayerShape.updateShapes(player, living);
                }

                // Refresh player dimensions
                player.refreshDimensions();
            });
        });
    }

    public static void sendSpecialSwapRequest() {
        NetworkManager.sendToServer(ClientNetworking.SPECIAL_SHAPE_REQUEST, new FriendlyByteBuf(Unpooled.buffer()));
    }
}
