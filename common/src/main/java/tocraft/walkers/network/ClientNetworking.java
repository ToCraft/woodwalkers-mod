package tocraft.walkers.network;

import dev.tocraft.craftedcore.client.CraftedCoreClient;
import dev.tocraft.craftedcore.network.ModernNetworking;
import dev.tocraft.craftedcore.network.client.ClientNetworking.ApplicablePacket;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.impl.SyncApiLevelPackets;
import tocraft.walkers.network.impl.UnlockPackets;

import java.util.Optional;
import java.util.UUID;

public class ClientNetworking implements NetworkHandler {

    public static void registerPacketHandlers() {
        ModernNetworking.registerReceiver(ModernNetworking.Side.S2C, NetworkHandler.SHAPE_SYNC,
                ClientNetworking::handleWalkersSyncPacket);
        ModernNetworking.registerReceiver(ModernNetworking.Side.S2C, NetworkHandler.ABILITY_SYNC,
                ClientNetworking::handleAbilitySyncPacket);
        ModernNetworking.registerReceiver(ModernNetworking.Side.S2C, NetworkHandler.UNLOCK_SYNC,
                UnlockPackets::handleUnlockSyncPacket);
        ModernNetworking.registerReceiver(ModernNetworking.Side.S2C, NetworkHandler.SYNC_API_LEVEL,
                (context, nbt) -> SyncApiLevelPackets.handleSyncPacket(nbt));
    }

    public static void runOrQueue(ModernNetworking.Context context, ApplicablePacket packet) {
        if (context.getPlayer() == null) {
            CraftedCoreClient.getSyncPacketQueue().add(packet);
        } else {
            context.queue(() -> packet.apply(context.getPlayer()));
        }
    }

    public static void sendAbilityRequest() {
        ModernNetworking.sendToServer(USE_ABILITY, new CompoundTag());
    }

    public static void handleWalkersSyncPacket(ModernNetworking.Context context, CompoundTag packetData) {
        final UUID uuid = UUIDUtil.uuidFromIntArray(packetData.getIntArray("uuid").orElseThrow());
        final String id = packetData.getString("type").orElseThrow();
        final CompoundTag entityNbt = packetData.getCompound("entity_tag").orElseThrow();

        runOrQueue(context, player -> {
            @Nullable
            Player syncTarget = player.level().getPlayerByUUID(uuid);

            if (syncTarget != null) {
                PlayerDataProvider data = (PlayerDataProvider) syncTarget;

                // set shape to null (no shape) if the entity id is "minecraft:empty"
                if (id.equals("minecraft:empty")) {
                    data.walkers$setCurrentShape(null);
                    syncTarget.refreshDimensions();
                    return;
                }

                // If entity type was valid, deserialize entity data from tag/
                entityNbt.putString("id", id);
                ValueInput in = TagValueInput.create(Walkers.PROBLEM_REPORTER, player.registryAccess(), entityNbt);
                Optional<EntityType<?>> type = EntityType.by(in);
                if (type.isPresent()) {
                    LivingEntity shape = data.walkers$getCurrentShape();

                    // ensure entity data exists
                    if (shape == null || !type.get().equals(shape.getType())) {
                        shape = (LivingEntity) type.get().create(syncTarget.level(), EntitySpawnReason.LOAD);
                        data.walkers$setCurrentShape(shape);

                        // refresh player dimensions/hitbox on client
                        syncTarget.refreshDimensions();
                    }

                    if (shape != null) {
                        shape.load(in);
                    }
                }
            }
        });
    }

    public static void handleAbilitySyncPacket(ModernNetworking.Context context, CompoundTag packet) {
        int cooldown = packet.getInt("cooldown").orElseThrow();
        runOrQueue(context, player -> ((PlayerDataProvider) player).walkers$setAbilityCooldown(cooldown));
    }
}
