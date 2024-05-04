package tocraft.walkers.api.data.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import tocraft.walkers.Walkers;

import java.util.HashMap;
import java.util.Map;

public abstract class SynchronizedJsonReloadListener extends SimpleJsonResourceReloadListener {
    protected final ResourceLocation RELOAD_SYNC;

    protected final String directory;
    protected final Gson gson;
    private final Map<ResourceLocation, JsonElement> map = new HashMap<>();

    public SynchronizedJsonReloadListener(Gson gson, String directory) {
        super(gson, directory);
        this.gson = gson;
        this.directory = directory;
        this.RELOAD_SYNC = Walkers.id("data_sync_" + directory);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.map.clear();
        this.map.putAll(map);
        this.onApply(map);
    }

    protected abstract void onApply(Map<ResourceLocation, JsonElement> map);

    public void sendSyncPacket(ServerPlayer player) {
        FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());

        // Serialize unlocked to tag
        CompoundTag compound = new CompoundTag();
        this.map.forEach((key, json) -> compound.putString(key.toString(), json.toString()));
        packet.writeNbt(compound);

        // Send to client
        NetworkManager.sendToPlayer(player, RELOAD_SYNC, packet);
    }

    @Environment(EnvType.CLIENT)
    private void onPacketReceive(FriendlyByteBuf packet, NetworkManager.PacketContext context) {
        this.map.clear();
        CompoundTag compound = packet.readNbt();
        if (compound != null) {
            for (String key : compound.getAllKeys()) {
                this.map.put(new ResourceLocation(key), JsonParser.parseString(compound.getString(key)));
            }
        }
        if (Platform.getEnv() == EnvType.CLIENT) {
            this.onApply(map);
        }
    }

    @Environment(EnvType.CLIENT)
    public void registerPacketReceiver() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, RELOAD_SYNC, this::onPacketReceive);
    }
}
