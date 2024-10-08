package tocraft.walkers.ability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import tocraft.craftedcore.event.client.RenderEvents;
import tocraft.craftedcore.gui.TimerOverlayRenderer;
import tocraft.walkers.api.PlayerAbilities;
import tocraft.walkers.api.PlayerShape;

public class AbilityOverlayRenderer {

    public static void register() {
        RenderEvents.HUD_RENDERING.register((matrices, delta) -> {
            Minecraft client = Minecraft.getInstance();
            LocalPlayer player = client.player;
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape == null) {
                return;
            }

            ShapeAbility<LivingEntity> shapeAbility = AbilityRegistry.get(shape);

            if (shapeAbility != null) {
                TimerOverlayRenderer.register(matrices, PlayerAbilities.getCooldown(player), shapeAbility.getCooldown(shape), shapeAbility.getIcon());
            }
        });
    }
}
