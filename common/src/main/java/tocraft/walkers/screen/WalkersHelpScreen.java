package tocraft.walkers.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class WalkersHelpScreen extends Screen {

    public WalkersHelpScreen() {
        super(Component.literal(""));
        super.init(Minecraft.getInstance(), Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        PoseStack matrices = context.pose();
        matrices.pushPose();
        matrices.scale(0.75f, 0.75f, 0.75f);
        context.drawString( Minecraft.getInstance().font, Component.translatable("walkers.help.welcome"), 15, 15, 0xffffff, true);
        context.drawString( Minecraft.getInstance().font, Component.translatable("walkers.help.credits"), 15, 30, 0xffffff, true);

        context.drawString( Minecraft.getInstance().font, Component.translatable("walkers.help.support_label").withStyle(ChatFormatting.BOLD), 15, 60, 0xffffff, true);
        context.drawString( Minecraft.getInstance().font, Component.translatable("walkers.help.support_description"), 15, 75, 0xffffff, true);

        context.drawString( Minecraft.getInstance().font, Component.translatable("walkers.help.ability_label").withStyle(ChatFormatting.BOLD), 15, 100, 0xffffff, true);
        context.drawString( Minecraft.getInstance().font, Component.translatable("walkers.help.ability_description_1"), 15, 115, 0xffffff, true);
        context.drawString( Minecraft.getInstance().font, Component.translatable("walkers.help.ability_description_2"), 15, 130, 0xffffff, true);
        context.drawString( Minecraft.getInstance().font, Component.translatable("walkers.help.ability_description_3"), 15, 145, 0xffffff, true);

        context.drawString( Minecraft.getInstance().font, Component.translatable("walkers.help.config_label").withStyle(ChatFormatting.BOLD), 15, 175, 0xffffff, true);
        context.drawString( Minecraft.getInstance().font, Component.translatable("walkers.help.config_description"), 15, 190, 0xffffff, true);

        context.drawString( Minecraft.getInstance().font, Component.translatable("walkers.help.credits_label").withStyle(ChatFormatting.BOLD), 15, 220, 0xffffff, true);
        context.drawString( Minecraft.getInstance().font, Component.translatable("walkers.help.credits_general"), 15, 235, 0xffffff, true);
        context.drawString( Minecraft.getInstance().font, Component.translatable("walkers.help.credits_translators"), 15, 250, 0xffffff, true);

        context.drawString( Minecraft.getInstance().font, Component.translatable("walkers.help.return").withStyle(ChatFormatting.ITALIC), 15, height + 60, 0xffffff, true);

        matrices.popPose();

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        onClose();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
