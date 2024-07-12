package tocraft.walkers.screen.hud;

//#if MC>1194
import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.event.client.RenderEvents;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.AttackForHealthTrait;

public class OverlayEventHandler {
    public static void initialize() {
        RenderEvents.RENDER_FOOD.register(new RenderFood());
    }

    private static class RenderFood implements RenderEvents.OverlayRendering {
        @Override
        //#if MC>1194
        public InteractionResult render(@Nullable GuiGraphics graphics, Player player) {
        //#else
        //$$ public InteractionResult render(@Nullable PoseStack graphics, Player player) {
        //#endif
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
