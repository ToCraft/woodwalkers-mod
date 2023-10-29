package tocraft.walkers.ability;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import tocraft.craftedcore.events.client.ClientGuiEvents;
import tocraft.craftedcore.gui.TimerOverlayRenderer;
import tocraft.walkers.api.PlayerAbilities;
import tocraft.walkers.api.PlayerShape;

public class AbilityOverlayRenderer {

    public static void register() {
        ClientGuiEvents.RENDER_HUD.register((matrices, delta) -> {
            Minecraft client = Minecraft.getInstance();
            LocalPlayer player = client.player;
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if(shape == null) {
                return;
            }

            WalkersAbility<? extends LivingEntity> shapeAbility = AbilityRegistry.get(shape.getType());

            if(shapeAbility == null) {
                return;
            }

            if(client.screen instanceof ChatScreen) {
                return;
            }

             TimerOverlayRenderer.register(matrices, PlayerAbilities.getCooldown(player), AbilityRegistry.get(shape.getType()).getCooldown(shape), shapeAbility.getIcon());
        });
    }
}
