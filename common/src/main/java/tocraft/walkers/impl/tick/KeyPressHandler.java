package tocraft.walkers.impl.tick;

import org.jetbrains.annotations.Nullable;

import dev.architectury.event.events.client.ClientTickEvent;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.platform.SyncedVars;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.impl.DevSwapPackets;
import tocraft.walkers.network.impl.SwapPackets;
import tocraft.walkers.screen.WalkersHelpScreen;
import tocraft.walkers.screen.WalkersScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class KeyPressHandler implements ClientTickEvent.Client {
    private float currentTimer = 0f;

    @Override
    public void tick(MinecraftClient client) {
        assert client.player != null;

        if(WalkersClient.ABILITY_KEY.wasPressed())
            handleAbilityKey(client);

        if(WalkersClient.MENU_KEY.wasPressed())
            handleMenuKey(client);

        if(WalkersClient.TRANSFORM_KEY.wasPressed()) {
            handleTransformKey(client);
        }

        if(WalkersClient.UNLOCK_KEY.isPressed()) {
            handleUnlockKey(client);
        }
        else if (currentTimer != SyncedVars.getUnlockTimer())
            currentTimer = SyncedVars.getUnlockTimer();
    }

    private void handleAbilityKey(MinecraftClient client) {
        // TODO: maybe the check should be on the server to allow for ability extension mods?
        // Only send the ability packet if the walkers equipped by the player has one
        LivingEntity walkers = PlayerShape.getCurrentShape(client.player);

        if(walkers != null) {
            if(AbilityRegistry.has(walkers.getType())) {
                ClientNetworking.sendAbilityRequest();
            }
        }
    }

    private void handleMenuKey(MinecraftClient client) {
        if (client.player.isSneaking() && (Walkers.devs.contains(client.player.getUuidAsString()) || client.player.hasPermissionLevel(2))) {
            DevSwapPackets.sendDevSwapRequest(new Identifier("minecraft:wolf"));
        }
        else {
            if ((((PlayerDataProvider) client.player).get2ndShape() == null || SyncedVars.getUnlockOveridesCurrentShape()) && !SyncedVars.getEnableUnlockSystem())
                MinecraftClient.getInstance().setScreen(new WalkersScreen());
            else 
                MinecraftClient.getInstance().setScreen(new WalkersHelpScreen());
        }
    }

    private void handleTransformKey(MinecraftClient client) {
        if (PlayerShape.getCurrentShape(client.player) == null)
            SwapPackets.sendSwapRequest(((PlayerDataProvider) client.player).get2ndShape(), false);
        else
            SwapPackets.sendSwapRequest(null, false);
    }

    private void handleUnlockKey(MinecraftClient client) {
        if (SyncedVars.getEnableUnlockSystem()) {
            HitResult hit = client.crosshairTarget;
            if ((((PlayerDataProvider)client.player).get2ndShape() == null || SyncedVars.getUnlockOveridesCurrentShape()) && hit instanceof EntityHitResult) {
                Entity entityHit = ((EntityHitResult) hit).getEntity();
                if(entityHit instanceof LivingEntity living) {
                    @Nullable ShapeType<?> type = ShapeType.from(living);

                    // Ensures, the mob isn't on the blacklist
                    if (!SyncedVars.getShapeBlacklist().isEmpty() && SyncedVars.getShapeBlacklist().contains(EntityType.getId(type.getEntityType()).toString()))
                        client.player.sendMessage(Text.translatable("walkers.unlock_entity_blacklisted"), true);
                    else {
                        if (currentTimer <= 0) {
                            // unlock shape
                            SwapPackets.sendSwapRequest(type, true);
                            // send unlock message
                            Text name = Text.translatable(type.getEntityType().getTranslationKey());
                            client.player.sendMessage(Text.translatable("walkers.unlock_entity", name), true);
                            currentTimer = SyncedVars.getUnlockTimer();
                        }
                        else {
                            client.player.sendMessage(Text.translatable("walkers.unlock_progress"), true);
                            currentTimer -= 1;
                        }
                    }
                }
            }
            else
                currentTimer = SyncedVars.getUnlockTimer();
        }
    }
}
