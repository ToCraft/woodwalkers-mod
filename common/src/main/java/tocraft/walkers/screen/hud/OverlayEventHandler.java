package tocraft.walkers.screen.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.event.client.RenderEvents;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.AttackForHealthSkill;
import tocraft.walkers.skills.impl.UndrownableSkill;

public class OverlayEventHandler {
    public static void initialize() {
        RenderEvents.RENDER_HEALTH.register(new RenderHealth());
        RenderEvents.RENDER_FOOD.register(new RenderFood());
    }

    private static class RenderHealth implements RenderEvents.OverlayRendering {
        @Override
        public InteractionResult render(@Nullable PoseStack graphics, Player player) {
            if (player != null) {
                LivingEntity shape = PlayerShape.getCurrentShape(player);

                if (shape != null) {
                    if (Walkers.isAquatic(shape) < 2 || SkillRegistry.has(shape, UndrownableSkill.ID) && player.isEyeInFluid(FluidTags.WATER)) {
                        return InteractionResult.FAIL;
                    }
                }
            }

            return InteractionResult.PASS;
        }
    }

    private static class RenderFood implements RenderEvents.OverlayRendering {
        @Override
        public InteractionResult render(@Nullable PoseStack graphics, Player player) {
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
