package dev.tocraft.walkers.screen.hud;

import dev.tocraft.craftedcore.event.client.RenderEvents;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.traits.TraitRegistry;
import dev.tocraft.walkers.traits.impl.AttackForHealthTrait;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class OverlayEventHandler {
    public static void initialize() {
        RenderEvents.RENDER_FOOD.register(new RenderFood());
    }

    private static class RenderFood implements RenderEvents.OverlayRendering {
        @Override
        public InteractionResult render(@Nullable GuiGraphics graphics, Player player) {
            if (player != null) {
                LivingEntity shape = PlayerShape.getCurrentShape(player);

                if (shape != null) {
                    if (TraitRegistry.has(shape, AttackForHealthTrait.ID)) {
                        return InteractionResult.FAIL;
                    }
                }
            }

            return InteractionResult.PASS;
        }
    }
}
