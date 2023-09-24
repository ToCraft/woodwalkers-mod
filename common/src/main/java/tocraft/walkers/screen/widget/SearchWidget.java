package tocraft.walkers.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class SearchWidget extends EditBox {

    public SearchWidget(float x, float y, float width, float height) {
        super(Minecraft.getInstance().font, (int) x, (int) y, (int) width, (int) height, Component.nullToEmpty(""));
    }
}
