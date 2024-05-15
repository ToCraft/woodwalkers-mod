package tocraft.walkers.screen.hud;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.event.client.RenderEvents;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.AttackForHealthSkill;

public class OverlayEventHandler {
    public static void initialize() {
        RenderEvents.RENDER_BREATH.register(new RenderBreath());
        RenderEvents.RENDER_FOOD.register(new RenderFood());
    }

    private static class RenderBreath implements RenderEvents.OverlayRendering {
        @Override
        public InteractionResult render(@Nullable GuiGraphics graphics, Player player) {
            if (player != null && player.getAirSupply() == player.getMaxAirSupply() && player.isEyeInFluid(FluidTags.WATER)) {
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        }
    }

    private static class RenderFood implements RenderEvents.OverlayRendering {
        @Override
        public InteractionResult render(@Nullable GuiGraphics graphics, Player player) {
            if (player != null) {
                LivingEntity shape = PlayerShape.getCurrentShape(player);

                if (shape != null) {
                    if (SkillRegistry.has(shape, AttackForHealthSkill.ID)) {
                        return InteractionResult.FAIL;
                    }
                }
            }

            return InteractionResult.PASS;
        }
    }
}
