package tocraft.walkers.screen.widget;

import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.network.impl.SwapPackets;
import tocraft.walkers.screen.WalkersScreen;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class EntityWidget<T extends LivingEntity> extends AbstractButton {

    private final ShapeType<T> type;
    private final T entity;
    private final int size;
    private final WalkersScreen parent;
    private boolean crashed;

    public EntityWidget(float x, float y, float width, float height, ShapeType<T> type, T entity, WalkersScreen parent) {
        super((int) x, (int) y, (int) width, (int) height, Component.nullToEmpty("")); // int x, int y, int width, int height, message
        this.type = type;
        this.entity = entity;
        size = (int) (25 * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth()))));
        entity.setGlowingTag(true);
        this.parent = parent;
        setTooltip(Tooltip.create(type.createTooltipText(entity)));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl = mouseX >= (double) this.getX() && mouseX < (double) (this.getX() + this.width) && mouseY >= (double) this.getY() && mouseY < (double) (this.getY() + this.height);
        if(bl) {
            // Update current Walkers
            SwapPackets.sendSwapRequest(type, true);
            parent.disableAll();
            // close active screen handler
            parent.onClose();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if(!crashed) {
            // Some entities (namely Aether mobs) crash when rendered in a GUI.
            // Unsure as to the cause, but this try/catch should prevent the game from entirely dipping out.
            try {
                InventoryScreen.renderEntityInInventoryFollowsMouse(context, this.getX() + this.getWidth() / 2, (int) (this.getY() + this.getHeight() * .75f), size, -10, -10, entity);
            } catch (Exception ignored) {
                crashed = true;
                MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
                immediate.endBatch();
                EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
                entityRenderDispatcher.setRenderShadow(true);
                RenderSystem.getModelViewStack().popPose();
                Lighting.setupFor3DItems();
            }
        }
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {

    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void onPress() {

    }

    @Override
public void updateWidgetNarration(NarrationElementOutput builder) {

    }
}
