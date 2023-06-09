package tocraft.walkers.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.mixin.accessor.ScreenAccessor;
import tocraft.walkers.screen.widget.EntityWidget;
import tocraft.walkers.screen.widget.HelpWidget;
import tocraft.walkers.screen.widget.SearchWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WalkersScreen extends Screen {

    private final List<ShapeType<?>> rendered = new ArrayList<>();
    private final Map<ShapeType<?>, MobEntity> renderEntities = new LinkedHashMap<>();
    private final List<EntityWidget> entityWidgets = new ArrayList<>();
    private final SearchWidget searchBar = createSearchBar();
    private final ButtonWidget helpButton = createHelpButton();
    private String lastSearchContents = "";

    public WalkersScreen() {
        super(Text.literal(""));
        super.init(MinecraftClient.getInstance(), MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());

        // don't initialize if the player is null
        if(client.player == null) {
            client.setScreen(null);
            return;
        }

        populateRenderEntities();
        addDrawableChild(searchBar);
        addDrawableChild(helpButton);

        rendered.addAll(collectEntities(client.player));

        // add entity widgets
        populateEntityWidgets(client.player, rendered);

        // implement search handler
        searchBar.setChangedListener(text -> {
            focusOn(searchBar);

            // Only re-filter if the text contents changed
            if(!lastSearchContents.equals(text)) {
                ((ScreenAccessor) this).getSelectables().removeIf(button -> button instanceof EntityWidget);
                children().removeIf(button -> button instanceof EntityWidget);
                entityWidgets.clear();

                List<ShapeType<?>> filtered = rendered
                        .stream()
                        .filter(type -> text.isEmpty() || type.getEntityType().getTranslationKey().contains(text))
                        .collect(Collectors.toList());

                populateEntityWidgets(client.player, filtered);
            }

            lastSearchContents = text;
        });
    }

    @Override
    public void clearChildren() {

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        searchBar.render(matrices, mouseX, mouseY, delta);
        helpButton.render(matrices, mouseX, mouseY, delta);
        renderEntityWidgets(matrices, mouseX, mouseY, delta);
    }

    public void renderEntityWidgets(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        double scaledFactor = this.client.getWindow().getScaleFactor();
        int top = 35;

        matrices.push();
        RenderSystem.enableScissor(
                (int) ((double) 0 * scaledFactor),
                (int) ((double) 0 * scaledFactor),
                (int) ((double) width * scaledFactor),
                (int) ((double) (this.height - top) * scaledFactor));

        entityWidgets.forEach(widget -> {
            widget.render(matrices, mouseX, mouseY, delta);
        });

        RenderSystem.disableScissor();

        matrices.pop();
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

    private void populateEntityWidgets(ClientPlayerEntity player, List<ShapeType<?>> unlocked) {
        // add widget for each unlocked entity
        int x = 15;
        int y = 35;
        int rows = (int) Math.ceil(unlocked.size() / 7f);

        for (int yIndex = 0; yIndex <= rows; yIndex++) {
            for (int xIndex = 0; xIndex < 7; xIndex++) {
                int listIndex = yIndex * 7 + xIndex;

                if(listIndex < unlocked.size()) {
                    ShapeType<?> type = unlocked.get(listIndex);

                    // TODO: only render selected type, this will show all eg. sheep
                    EntityWidget entityWidget = new EntityWidget(
                            (getWindow().getScaledWidth() - 27) / 7f * xIndex + x,
                            getWindow().getScaledHeight() / 5f * yIndex + y,
                            (getWindow().getScaledWidth() - 27) / 7f,
                            getWindow().getScaledHeight() / 5f,
                            type,
                            renderEntities.get(type),
                            this
                    );

                    entityWidget.setTooltip(Tooltip.of(Text.translatable(renderEntities.get(type).getType().getTranslationKey())));

                    addDrawableChild(entityWidget);
                    entityWidgets.add(entityWidget);
                }
            }
        }
    }

    private void populateRenderEntities() {
        if(renderEntities.isEmpty()) {
            List<ShapeType<?>> types = ShapeType.getAllTypes(MinecraftClient.getInstance().world);
            for (ShapeType<?> type : types) {
                Entity entity = type.create(MinecraftClient.getInstance().world);
                if(entity instanceof MobEntity living) {
                    renderEntities.put(type, living);
                }
            }

            Walkers.LOGGER.info(String.format("Loaded %d entities for rendering", types.size()));
        }
    }

    private List<ShapeType<?>> collectEntities(ClientPlayerEntity player) {
        List<ShapeType<?>> entities = new ArrayList<>();

        // collect current unlocked second shape
        renderEntities.forEach((type, instance) -> {
            entities.add(type);
        });

        return entities;
    }

    private SearchWidget createSearchBar() {
        return new SearchWidget(
                getWindow().getScaledWidth() / 2f - (getWindow().getScaledWidth() / 4f / 2) - 5,
                5,
                getWindow().getScaledWidth() / 4f,
                20f);
    }

    private ButtonWidget createHelpButton() {
        HelpWidget helpWidget = new HelpWidget(
                (int) (getWindow().getScaledWidth() / 2f + (getWindow().getScaledWidth() / 8f) + 5),
                7,
                20,
                20);
        helpWidget.setTooltip(Tooltip.of(Text.translatable("walkers.help")));
        return helpWidget;
    }

    public Window getWindow() {
        return MinecraftClient.getInstance().getWindow();
    }

    public void disableAll() {
        entityWidgets.forEach(button -> button.setActive(false));
    }

    @Override
    public boolean shouldPause() {
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
