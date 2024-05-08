package tocraft.walkers.network.impl;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.craftedcore.network.ModernNetworking;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.api.variant.TypeProviderRegistry;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.NetworkHandler;

public class SwapVariantPackets {

    @SuppressWarnings("ConstantConditions")
    public static void registerSwapVariantPacketHandler() {
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, NetworkHandler.VARIANT_REQUEST,
                (context, packet) -> {
                    int variantID = packet.getInt("variant_id");
                    context.getPlayer().getServer().execute(() -> {
                        if (Walkers.CONFIG.unlockEveryVariant) {
                            ShapeType<?> currentShapeType = ShapeType.from(PlayerShape.getCurrentShape(context.getPlayer()));

                            TypeProvider<?> typeProvider = TypeProviderRegistry.getProvider(currentShapeType.getEntityType());
                            int range = typeProvider != null ? typeProvider.getRange() : -1;

                            // switch to special shape
                            if (Walkers.hasSpecialShape(context.getPlayer().getUUID()) && BuiltInRegistries.ENTITY_TYPE.getKey(currentShapeType.getEntityType()).equals(new ResourceLocation("minecraft:wolf")) && variantID == range + 1) {
                                Entity created;
                                CompoundTag nbt = new CompoundTag();

                                nbt.putBoolean("isSpecial", true);
                                nbt.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(currentShapeType.getEntityType()).toString());
                                created = EntityType.loadEntityRecursive(nbt, context.getPlayer().level, it -> it);
                                PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), (LivingEntity) created);
                            }
                            // switch normally
                            else {
                                if (currentShapeType != null && currentShapeType.getVariantData() != variantID) {
                                    ShapeType<?> newShapeType = ShapeType.from(currentShapeType.getEntityType(), variantID);
                                    if (newShapeType != null) {
                                        if (PlayerShapeChanger.change2ndShape((ServerPlayer) context.getPlayer(), newShapeType)) {
                                            LivingEntity shape = newShapeType.create(context.getPlayer().level, context.getPlayer());
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
            CompoundTag packet = new CompoundTag();
            packet.putInt("variant_id", variantID);
            ModernNetworking.sendToServer(ClientNetworking.VARIANT_REQUEST, packet);
        }
    }
}
