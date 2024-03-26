package tocraft.walkers.screen.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.api.variant.TypeProviderRegistry;
import tocraft.walkers.impl.PlayerDataProvider;

import java.util.HashMap;
import java.util.Map;

public class VariantMenu {
    private final Map<ShapeType<?>, LivingEntity> walkers$renderedEntities = new HashMap<>();

    public void render(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.hideGui && WalkersClient.renderVariantsMenu && Walkers.CONFIG.unlockEveryVariant && minecraft.screen == null) {
            Level level = minecraft.level;
            if (level != null && minecraft.player != null) {
                ShapeType<?> currentShapeType = ShapeType.from(((PlayerDataProvider) minecraft.player).walkers$getCurrentShape());
                if (currentShapeType != null) {
                    int currentVariantId = currentShapeType.getVariantData();

                    // get data of menu
                    int x = guiGraphics.guiWidth() / 7;
                    int y = guiGraphics.guiHeight() / 5;
                    // render transparent background
                    guiGraphics.fillGradient(x, 0, x * 6, y + 10, -1072689136, -804253680);
                    // render entities
                    TypeProvider<?> typeProvider = TypeProviderRegistry.getProvider(currentShapeType.getEntityType());
                    if (typeProvider != null) {
                        if (WalkersClient.variantOffset < -currentVariantId)
                            WalkersClient.variantOffset = -currentVariantId;
                        else if (WalkersClient.variantOffset > typeProvider.getRange() - currentVariantId)
                            WalkersClient.variantOffset = typeProvider.getRange() - currentVariantId;
                        for (int i = 1; i <= 5; i++) {
                            int thisVariantId = currentVariantId - 3 + i + WalkersClient.variantOffset;
                            if (thisVariantId >= 0 && (thisVariantId <= typeProvider.getRange() || thisVariantId == currentVariantId)) {
                                ShapeType<?> thisShapeType = ShapeType.from(currentShapeType.getEntityType(), thisVariantId);
                                if (thisShapeType != null) {
                                    LivingEntity entity = walkers$renderedEntities.get(thisShapeType);
                                    if (entity == null) {
                                        entity = thisShapeType.create(level, minecraft.player);
                                        if (entity != null) {
                                            walkers$renderedEntities.put(thisShapeType, entity);
                                        }
                                    }
                                    if (entity != null) {
                                        InventoryScreen.renderEntityInInventory(guiGraphics, (float) x * i + (float) x / 2, (float) y * .75f, (int) (25 * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth())))), new Vector3f(), new Quaternionf().rotationXYZ(0.43633232F, (float) Math.PI, (float) Math.PI), null, entity);
                                    }
                                }
                            }
                        }
                    } else {
                        LivingEntity entity = currentShapeType.create(level);
                        if (entity != null) {
                            InventoryScreen.renderEntityInInventory(guiGraphics, (float) x * 3 + (float) x / 2, (float) y * .75f, (int) (25 * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth())))), new Vector3f(), new Quaternionf().rotationXYZ(0.43633232F, (float) Math.PI, (float) Math.PI), null, entity);
                        }
                    }
                    // render focus
                    guiGraphics.blit(Walkers.id("textures/gui/focused.png"), x * 3, 5, x, y, 0, 0, 48, 32, 48, 32);
                }
            }
        }
    }
}
