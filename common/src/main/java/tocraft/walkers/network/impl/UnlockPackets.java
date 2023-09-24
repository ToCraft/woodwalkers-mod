package tocraft.walkers.network.impl;

import dev.architectury.networking.NetworkManager;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class UnlockPackets {

    private static final String UNLOCK_KEY = "UnlockedShape";

    public static void handleUnlockSyncPacket(FriendlyByteBuf packet, NetworkManager.PacketContext context) {
        CompoundTag nbt = packet.readNbt();
        if(nbt != null) {
            CompoundTag idTag = nbt.getCompound(UNLOCK_KEY);

            ClientNetworking.runOrQueue(context, player -> {
                if (idTag != null) ((PlayerDataProvider) player).set2ndShape(ShapeType.from(idTag));
            });
        }
    }

    public static void sendSyncPacket(ServerPlayer player) {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());

        // Serialize unlocked to tag
        CompoundTag compound = new CompoundTag();
        CompoundTag id = new CompoundTag();
        if (((PlayerDataProvider) player).get2ndShape() != null)
            id = ((PlayerDataProvider) player).get2ndShape().writeCompound();
        compound.put(UNLOCK_KEY, id);
        packet.writeNbt(compound);

        // Send to client
        NetworkManager.sendToPlayer(player, NetworkHandler.UNLOCK_SYNC, packet);
    }
}
