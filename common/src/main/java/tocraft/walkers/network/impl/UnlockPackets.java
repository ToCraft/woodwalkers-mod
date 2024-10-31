package tocraft.walkers.network.impl;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.network.ModernNetworking;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.blacklist.EntityBlacklist;
import tocraft.walkers.api.platform.ApiLevel;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;

public class UnlockPackets {

    private static final String UNLOCK_KEY = "UnlockedShape";

    public static void handleUnlockSyncPacket(ModernNetworking.Context context, CompoundTag nbt) {
        if (nbt != null && ApiLevel.getCurrentLevel().canUnlock) {
            CompoundTag idTag = nbt.getCompound(UNLOCK_KEY);

            ClientNetworking.runOrQueue(context, player -> {
                if (!idTag.isEmpty())
                    ((PlayerDataProvider) player).walkers$set2ndShape(ShapeType.from(idTag));
            });
        }
    }

    /**
     * Server handles request, that 2nd shape may be changed
     */
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static void registerShapeUnlockRequestPacketHandler() {
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, NetworkHandler.UNLOCK_REQUEST, (context, nbt) -> {
            // check if player is blacklisted
            if (Walkers.isPlayerBlacklisted(context.getPlayer().getUUID()) && Walkers.CONFIG.blacklistPreventsUnlocking) {
                return;
            }

            if (!ApiLevel.getCurrentLevel().canUnlock) {
                return;
            }

            boolean validType = nbt.getBoolean("valid_type");
            if (validType) {
                ResourceLocation typeId = ResourceLocation.parse(nbt.getString("type_id"));
                EntityType<? extends LivingEntity> entityType = (EntityType<? extends LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(typeId).orElseThrow().value();

                int variant = nbt.getInt("variant");

                context.getPlayer().getServer().execute(() -> {
                    @Nullable
                    ShapeType<? extends LivingEntity> type = ShapeType.from(entityType, variant);
                    if (type != null && !EntityBlacklist.isBlacklisted(type.getEntityType()) && (Walkers.CONFIG.unlockOverridesCurrentShape || ((PlayerDataProvider) context.getPlayer()).walkers$get2ndShape() == null)) {
                        // set 2nd shape
                        boolean result = PlayerShapeChanger.change2ndShape((ServerPlayer) context.getPlayer(), type);
                        // update Player
                        if (result)
                            PlayerShape.updateShapes((ServerPlayer) context.getPlayer(),
                                    type.create(context.getPlayer().level(), context.getPlayer()));
                    }
                });
            } else {
                // Swap back to player if server allows it
                context.getPlayer().getServer().execute(() -> PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), null));
            }

            // Refresh player dimensions
            context.getPlayer().refreshDimensions();
        });
    }

    /**
     * Server synchronizes unlocked shape with the client
     */
    public static void sendSyncPacket(ServerPlayer player) {
        // Serialize unlocked to tag
        CompoundTag compound = new CompoundTag();
        CompoundTag id = new CompoundTag();
        if (((PlayerDataProvider) player).walkers$get2ndShape() != null)
            id = ((PlayerDataProvider) player).walkers$get2ndShape().writeCompound();
        compound.put(UNLOCK_KEY, id);

        // Send to client
        ModernNetworking.sendToPlayer(player, NetworkHandler.UNLOCK_SYNC, compound);
    }

    /**
     * Client requests, that server may unlock a shape
     */
    public static void sendUnlockRequest(@Nullable ShapeType<? extends LivingEntity> type) {
        CompoundTag packet = new CompoundTag();

        packet.putBoolean("valid_type", type != null);
        if (type != null) {
            packet.putString("type_id", EntityType.getKey(type.getEntityType()).toString());
            packet.putInt("variant", type.getVariantData());
        }

        ModernNetworking.sendToServer(ClientNetworking.UNLOCK_REQUEST, packet);
    }
}
