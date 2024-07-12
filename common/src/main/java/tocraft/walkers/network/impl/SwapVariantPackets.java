package tocraft.walkers.network.impl;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.craftedcore.network.ModernNetworking;
import tocraft.craftedcore.patched.CEntity;
import tocraft.craftedcore.patched.Identifier;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.platform.ApiLevel;
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
                    if (!ApiLevel.getCurrentLevel().allowVariantsMenu) {
                        return;
                    }

                    int variantID = packet.getInt("variant_id");
                    context.getPlayer().getServer().execute(() -> {
                        if (Walkers.CONFIG.unlockEveryVariant) {
                            ShapeType<?> currentShapeType = ShapeType.from(PlayerShape.getCurrentShape(context.getPlayer()));

                            TypeProvider<?> typeProvider = TypeProviderRegistry.getProvider(currentShapeType.getEntityType());
                            int range = typeProvider != null ? typeProvider.getRange() : -1;

                            // switch to special shape
                            if (Walkers.hasSpecialShape(context.getPlayer().getUUID()) && Walkers.getEntityTypeRegistry().getKey(currentShapeType.getEntityType()).equals(Identifier.parse("minecraft:wolf")) && variantID == range + 1) {
                                Entity created;
                                CompoundTag nbt = new CompoundTag();

                                nbt.putBoolean("isSpecial", true);
                                nbt.putString("id", Walkers.getEntityTypeRegistry().getKey(currentShapeType.getEntityType()).toString());
                                created = EntityType.loadEntityRecursive(nbt, CEntity.level(context.getPlayer()), it -> it);
                                PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), (LivingEntity) created);
                            }
                            // switch normally
                            else {
                                if (currentShapeType != null && currentShapeType.getVariantData() != variantID) {
                                    ShapeType<?> newShapeType = ShapeType.from(currentShapeType.getEntityType(), variantID);
                                    if (newShapeType != null) {
                                        if (PlayerShapeChanger.change2ndShape((ServerPlayer) context.getPlayer(), newShapeType) || !ApiLevel.getCurrentLevel().canUnlock) {
                                            LivingEntity shape = newShapeType.create(CEntity.level(context.getPlayer()), context.getPlayer());
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
