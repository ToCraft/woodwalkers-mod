package tocraft.walkers.screen;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.mixin.accessor.ScreenAccessor;
import tocraft.walkers.screen.widget.EntityWidget;
import tocraft.walkers.screen.widget.HelpWidget;
import tocraft.walkers.screen.widget.SearchWidget;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class WalkersScreen extends Screen {

    private final List<ShapeType<?>> rendered = new ArrayList<>();
    private final Map<ShapeType<?>, Mob> renderEntities = new LinkedHashMap<>();
    private final List<EntityWidget<?>> entityWidgets = new ArrayList<>();
    private final SearchWidget searchBar = createSearchBar();
    private final Button helpButton = createHelpButton();
    private String lastSearchContents = "";

    public WalkersScreen() {
        super(Component.literal(""));
        super.init(Minecraft.getInstance(), Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());

        // don't initialize if the player is null
        if(minecraft.player == null) {
            minecraft.setScreen(null);
            return;
        }

        populateRenderEntities();
        addRenderableWidget(searchBar);
        addRenderableWidget(helpButton);

        rendered.addAll(collectEntities(minecraft.player));

        // add entity widgets
        populateEntityWidgets(minecraft.player, rendered);

        // implement search handler
        searchBar.setResponder(text -> {
            magicalSpecialHackyFocus(searchBar);

            // Only re-filter if the text contents changed
            if(!lastSearchContents.equals(text)) {
                ((ScreenAccessor) this).getSelectables().removeIf(button -> button instanceof EntityWidget);
                children().removeIf(button -> button instanceof EntityWidget);
                entityWidgets.clear();

                List<ShapeType<?>> filtered = rendered
                        .stream()
                        .filter(type -> text.isEmpty() || type.getEntityType().getDescriptionId().contains(text))
                        .collect(Collectors.toList());

                populateEntityWidgets(minecraft.player, filtered);
            }

            lastSearchContents = text;
        });
    }

    @Override
    public void clearWidgets() {

    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        searchBar.render(context, mouseX, mouseY, delta);
        helpButton.render(context, mouseX, mouseY, delta);
        renderEntityWidgets(context, mouseX, mouseY, delta);
    }

    public void renderEntityWidgets(GuiGraphics context, int mouseX, int mouseY, float delta) {
        double scaledFactor = this.minecraft.getWindow().getGuiScale();
        int top = 35;

        context.pose().pushPose();
        RenderSystem.enableScissor(
                (int) ((double) 0 * scaledFactor),
                (int) ((double) 0 * scaledFactor),
                (int) ((double) width * scaledFactor),
                (int) ((double) (this.height - top) * scaledFactor));

        entityWidgets.forEach(widget -> {
            widget.render(context, mouseX, mouseY, delta);
        });

        RenderSystem.disableScissor();

        context.pose().popPose();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(entityWidgets.size() > 0) {
            float firstPos = entityWidgets.get(0).getY();

            // Top section should always have mobs, prevent scrolling the entire list down the screen
            if(amount == 1 && firstPos >= 35) {
                return false;
            }

            ((ScreenAccessor) this).getSelectables().forEach(button -> {
                if(button instanceof EntityWidget widget) {
                    widget.setPosition(widget.getX(), (int) (widget.getY() + amount * 10));
                }
            });
        }

        return false;
    }

    private void populateEntityWidgets(LocalPlayer player, List<ShapeType<?>> rendered) {
        // add widget for each entity to be rendered
        int x = 15;
        int y = 35;
        int rows = (int) Math.ceil(rendered.size() / 7f);

        for (int yIndex = 0; yIndex <= rows; yIndex++) {
            for (int xIndex = 0; xIndex < 7; xIndex++) {
                int listIndex = yIndex * 7 + xIndex;

                if(listIndex < rendered.size()) {
                    ShapeType<?> type = rendered.get(listIndex);

                    // TODO: only render selected type, this will show all eg. sheep
                    EntityWidget<?> entityWidget = new EntityWidget(
                            (getWindow().getGuiScaledWidth() - 27) / 7f * xIndex + x,
                            getWindow().getGuiScaledHeight() / 5f * yIndex + y,
                            (getWindow().getGuiScaledWidth() - 27) / 7f,
                            getWindow().getGuiScaledHeight() / 5f,
                            type,
                            renderEntities.get(type),
                            this
                    );

                    addRenderableWidget(entityWidget);
                    entityWidgets.add(entityWidget);
                }
            }
        }
    }

    private void populateRenderEntities() {
        if(renderEntities.isEmpty()) {
            List<ShapeType<?>> types = ShapeType.getAllTypes(Minecraft.getInstance().level);
            for (ShapeType<?> type : types) {
                Entity entity = type.create(Minecraft.getInstance().level);
                if(entity instanceof Mob living) {
                    renderEntities.put(type, living);
                }
            }

            Walkers.LOGGER.info(String.format("Loaded %d entities for rendering", types.size()));
        }
    }

    private List<ShapeType<?>> collectEntities(LocalPlayer player) {
        List<ShapeType<?>> entities = new ArrayList<>();

        // collect current unlocked second shape
        renderEntities.forEach((type, instance) -> {
            entities.add(type);
        });

        return entities;
    }

    private SearchWidget createSearchBar() {
        return new SearchWidget(
                getWindow().getGuiScaledWidth() / 2f - (getWindow().getGuiScaledWidth() / 4f / 2) - 5,
                5,
                getWindow().getGuiScaledWidth() / 4f,
                20f);
    }

    private Button createHelpButton() {
        HelpWidget helpWidget = new HelpWidget(
                (int) (getWindow().getGuiScaledWidth() / 2f + (getWindow().getGuiScaledWidth() / 8f) + 5),
                7,
                20,
                20);
        helpWidget.setTooltip(Tooltip.create(Component.translatable("walkers.help")));
        return helpWidget;
    }

    public Window getWindow() {
        return Minecraft.getInstance().getWindow();
    }

    public void disableAll() {
        entityWidgets.forEach(button -> button.setActive(false));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseY < 35) {
            return searchBar.mouseClicked(mouseX, mouseY, button) || helpButton.mouseClicked(mouseX, mouseY, button);
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }
}
