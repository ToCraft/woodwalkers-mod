package dev.tocraft.walkers.impl.tick;

import dev.tocraft.craftedcore.event.client.ClientTickEvents;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.WalkersClient;
import dev.tocraft.walkers.ability.AbilityRegistry;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.api.blacklist.EntityBlacklist;
import dev.tocraft.walkers.api.platform.ApiLevel;
import dev.tocraft.walkers.api.variant.ShapeType;
import dev.tocraft.walkers.impl.PlayerDataProvider;
import dev.tocraft.walkers.network.ClientNetworking;
import dev.tocraft.walkers.network.impl.SwapPackets;
import dev.tocraft.walkers.network.impl.SwapVariantPackets;
import dev.tocraft.walkers.network.impl.UnlockPackets;
import dev.tocraft.walkers.screen.hud.VariantMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class KeyPressHandler implements ClientTickEvents.Client {
    private float currentTimer = 0f;

    @Override
    public void tick(@NotNull Minecraft client) {
        if (client.player != null) {
            if (WalkersClient.ABILITY_KEY.consumeClick()) {
                handleAbilityKey(client);
            }

            if (WalkersClient.TRANSFORM_KEY.consumeClick()) {
                if (ApiLevel.getCurrentLevel().canMorph) {
                    SwapPackets.sendSwapRequest();
                } else {
                    client.player.displayClientMessage(Component.translatable("walkers.feature_not_available"), true);
                }
            }

            if (WalkersClient.VARIANTS_MENU_KEY.consumeClick() && Walkers.CONFIG.unlockEveryVariant) {
                if (ApiLevel.getCurrentLevel().allowVariantsMenu) {
                    LivingEntity shape = PlayerShape.getCurrentShape(client.player);
                    if (shape != null) {
                        ShapeType<?> shapeType = ShapeType.from(shape);
                        if (WalkersClient.isRenderingVariantsMenu) {
                            SwapVariantPackets.sendSwapRequest(shapeType.getVariantData() + WalkersClient.variantOffset);
                            VariantMenu.clearEntities();
                        }
                        WalkersClient.variantOffset = 0;
                        WalkersClient.isRenderingVariantsMenu = !WalkersClient.isRenderingVariantsMenu;
                    }
                } else {
                    client.player.displayClientMessage(Component.translatable("walkers.feature_not_available"), true);
                }
            }

            // disable variants menu when in other menu
            if (WalkersClient.isRenderingVariantsMenu && (client.options.hideGui || !Walkers.CONFIG.unlockEveryVariant || client.screen != null || PlayerShape.getCurrentShape(client.player) == null))
                WalkersClient.isRenderingVariantsMenu = false;

            if (WalkersClient.UNLOCK_KEY.isDown()) {
                if (ApiLevel.getCurrentLevel().canUnlock) {
                    handleUnlockKey(client);
                } else {
                    client.player.displayClientMessage(Component.translatable("walkers.feature_not_available"), true);
                }
            } else if (currentTimer != Walkers.CONFIG.unlockTimer) currentTimer = Walkers.CONFIG.unlockTimer;
        }
    }

    private void handleAbilityKey(@NotNull Minecraft client) {
        LivingEntity shape = PlayerShape.getCurrentShape(client.player);

        if (shape != null) {
            if (AbilityRegistry.has(shape)) {
                ClientNetworking.sendAbilityRequest();
            }
        }
    }

    //TODO: Merge this into something similar to the OnInteractEvent
    @SuppressWarnings("ConstantConditions")
    private void handleUnlockKey(@NotNull Minecraft client) {
        // check if player is blacklisted
        if (client.player != null && Walkers.isPlayerBlacklisted(client.player.getUUID()) && Walkers.CONFIG.blacklistPreventsUnlocking) {
            client.player.displayClientMessage(Component.translatable("walkers.player_blacklisted"), true);
            return;
        }

        HitResult hit = client.hitResult;
        if (client.player != null && (((PlayerDataProvider) client.player).walkers$get2ndShape() == null || Walkers.CONFIG.unlockOverridesCurrentShape) && hit instanceof EntityHitResult) {
            Entity entityHit = ((EntityHitResult) hit).getEntity();
            if (entityHit instanceof LivingEntity living) {
                @Nullable ShapeType<?> type = ShapeType.from(living);

                if (type != null) {
                    // Ensures, the mob isn't on the blacklist
                    if (EntityBlacklist.isBlacklisted(type.getEntityType()))
                        client.player.displayClientMessage(Component.translatable("walkers.unlock_entity_blacklisted"), true);
                    else {
                        if (currentTimer <= 0) {
                            // unlock shape
                            UnlockPackets.sendUnlockRequest(type);
                            // send unlock message
                            Component name = Component.translatable(type.getEntityType().getDescriptionId());
                            client.player.displayClientMessage(Component.translatable("walkers.unlock_entity", name), true);
                            currentTimer = Walkers.CONFIG.unlockTimer;
                        } else {
                            client.player.displayClientMessage(Component.translatable("walkers.unlock_progress"), true);
                            currentTimer -= 1;
                        }
                    }
                }
            }
        } else currentTimer = Walkers.CONFIG.unlockTimer;
    }
}
