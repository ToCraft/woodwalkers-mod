package tocraft.walkers.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import tocraft.walkers.screen.WalkersHelpScreen;

public class HelpWidget extends Button {

    public HelpWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.nullToEmpty("?"), (widget) -> {
            Minecraft.getInstance().setScreen(new WalkersHelpScreen());
        }, null);
    }
}
