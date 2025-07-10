package dev.tocraft.walkers.ability;

import dev.tocraft.craftedcore.event.client.RenderEvents;
import dev.tocraft.walkers.api.PlayerAbilities;
import dev.tocraft.walkers.api.PlayerShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AbilityOverlayRenderer {

    public static void register() {
        RenderEvents.HUD_RENDERING.register((graphics, delta) -> {

            Minecraft client = Minecraft.getInstance();
            LocalPlayer player = client.player;
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape == null) {
                return;
            }

            ShapeAbility<LivingEntity> shapeAbility = AbilityRegistry.get(shape);

            if (player != null && shapeAbility != null) {

                Item icon = shapeAbility.getIcon();
                if (icon != null) {

                    int currentCooldown = PlayerAbilities.getCooldown(player);
                    int maxCooldown = shapeAbility.getCooldown(shape);

                    if (client.screen instanceof ChatScreen || currentCooldown <= 0) {
                        return;
                    }

                    double d = client.getWindow().getGuiScale();
                    float cooldownScale = 1 - currentCooldown / (float) maxCooldown;

                    if (client.player != null) {
                        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
                        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();

                        // Calculate item position
                        int itemX = (int) (width * .95f);
                        int itemY = (int) (height * .92f);
                        int itemSize = 16; // Standard item size in pixels

                        graphics.pose().pushMatrix();
                        if (cooldownScale != 1) {
                            // Calculate the visible height of the item
                            int visibleHeight = (int) (itemSize * cooldownScale);

                            graphics.enableScissor(itemX,
                                    itemY + visibleHeight,
                                    itemX + itemSize,
                                    itemY + itemSize);
                        }

                        ItemStack stack = new ItemStack(icon);
                        graphics.renderItem(stack, (int) (width * .95f), (int) (height * .92f));

                        if (cooldownScale != 1) {
                            graphics.disableScissor();
                        }

                        graphics.pose().popMatrix();
                    }
                }
            }
        });
    }
}
