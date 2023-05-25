package tocraft.walkers.network.impl;

import dev.architectury.networking.NetworkManager;
import tocraft.walkers.api.variant.WalkersType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class UnlockPackets {

    private static final String UNLOCK_KEY = "UnlockedShape";

    public static void handleUnlockSyncPacket(PacketByteBuf packet, NetworkManager.PacketContext context) {
        NbtCompound nbt = packet.readNbt();
        if(nbt != null) {
            NbtCompound idTag = nbt.getCompound(UNLOCK_KEY);

            ClientNetworking.runOrQueue(context, player -> {
                ((PlayerDataProvider) player).get2ndShape().clear();
                ((PlayerDataProvider) player).get2ndShape().add(WalkersType.from((NbtCompound) idTag));
            });
        }
    }

    public static void sendSyncPacket(ServerPlayerEntity player) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());

        // Serialize unlocked to tag
        NbtCompound compound = new NbtCompound();
        ((PlayerDataProvider) player).get2ndShape().forEach(type -> {
            NbtCompound id = new NbtCompound();
            id = type.writeCompound();
            compound.put(UNLOCK_KEY, id);

        });
        packet.writeNbt(compound);

        // Send to client
        NetworkManager.sendToPlayer(player, NetworkHandler.UNLOCK_SYNC, packet);
    }
}
