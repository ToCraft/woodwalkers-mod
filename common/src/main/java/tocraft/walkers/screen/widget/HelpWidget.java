package tocraft.walkers.screen.widget;

import tocraft.walkers.screen.WalkersHelpScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class HelpWidget extends ButtonWidget {

    public HelpWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.of("?"), (widget) -> {
            MinecraftClient.getInstance().setScreen(new WalkersHelpScreen());
        }, null);
    }
}
