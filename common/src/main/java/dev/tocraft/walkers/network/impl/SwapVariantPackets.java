package dev.tocraft.walkers.network.impl;

import dev.tocraft.craftedcore.network.ModernNetworking;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.api.PlayerShapeChanger;
import dev.tocraft.walkers.api.platform.ApiLevel;
import dev.tocraft.walkers.api.variant.ShapeType;
import dev.tocraft.walkers.api.variant.TypeProvider;
import dev.tocraft.walkers.api.variant.TypeProviderRegistry;
import dev.tocraft.walkers.network.ClientNetworking;
import dev.tocraft.walkers.network.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class SwapVariantPackets {

    @SuppressWarnings("ConstantConditions")
    public static void registerSwapVariantPacketHandler() {
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, NetworkHandler.VARIANT_REQUEST,
                (context, packet) -> {
                    if (!ApiLevel.getCurrentLevel().allowVariantsMenu) {
                        return;
                    }

                    int variantID = packet.getInt("variant_id").orElse(-1);
                    context.getPlayer().getServer().execute(() -> {
                        if (Walkers.CONFIG.unlockEveryVariant) {
                            ShapeType<?> currentShapeType = ShapeType.from(PlayerShape.getCurrentShape(context.getPlayer()));

                            TypeProvider<?> typeProvider = TypeProviderRegistry.getProvider(currentShapeType.getEntityType());
                            int range = typeProvider != null ? typeProvider.size(context.getPlayer().level()) : -1;

                            // switch to special shape
                            if (Walkers.hasSpecialShape(context.getPlayer().getUUID()) && EntityType.getKey(currentShapeType.getEntityType()).equals(ResourceLocation.parse("minecraft:wolf")) && variantID == range) {
                                Entity created;
                                CompoundTag nbt = new CompoundTag();

                                nbt.putBoolean("isSpecial", true);
                                nbt.putString("id", EntityType.getKey(currentShapeType.getEntityType()).toString());
                                created = EntityType.loadEntityRecursive(nbt, context.getPlayer().level(), EntitySpawnReason.LOAD, it -> it);
                                PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), (LivingEntity) created);
                            }
                            // switch normally
                            else {
                                // DO NOT check if variant is already equipped; some variants are biome based!
                                if (currentShapeType != null) {
                                    ShapeType<?> newShapeType = ShapeType.from(currentShapeType.getEntityType(), variantID);
                                    if (newShapeType != null) {
                                        if (PlayerShapeChanger.change2ndShape((ServerPlayer) context.getPlayer(), newShapeType) || !ApiLevel.getCurrentLevel().canUnlock) {
                                            LivingEntity shape = newShapeType.create(context.getPlayer().level(), context.getPlayer());
                                            if (shape != null) {
                                                PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), shape);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                });
    }

    public static void sendSwapRequest(int variantID) {
        if (Walkers.CONFIG.unlockEveryVariant) {
            if (!ApiLevel.getCurrentLevel().allowVariantsMenu) {
                return;
            }

            CompoundTag packet = new CompoundTag();
            packet.putInt("variant_id", variantID);
            ModernNetworking.sendToServer(ClientNetworking.VARIANT_REQUEST, packet);
        }
    }
}
