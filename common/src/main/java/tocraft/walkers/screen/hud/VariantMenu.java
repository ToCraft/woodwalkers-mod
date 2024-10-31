package tocraft.walkers.screen.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.api.variant.TypeProviderRegistry;

import java.util.HashMap;
import java.util.Map;

public class VariantMenu {
    private final Map<ShapeType<?>, LivingEntity> renderEntities = new HashMap<>();
    private final Map<ShapeType<?>, LivingEntity> renderSpecialEntities = new HashMap<>();

    public void render(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.hideGui && WalkersClient.isRenderingVariantsMenu && Walkers.CONFIG.unlockEveryVariant && minecraft.screen == null) {
            Level level = minecraft.level;
            if (level != null && minecraft.player != null) {
                ShapeType<?> currentShapeType = ShapeType.from(PlayerShape.getCurrentShape(minecraft.player));
                if (currentShapeType != null) {
                    boolean hasSpecialVariant = Walkers.hasSpecialShape(minecraft.player.getUUID()) && EntityType.getKey(currentShapeType.getEntityType()).equals(ResourceLocation.parse("minecraft:wolf"));

                    int currentVariantId = currentShapeType.getVariantData();

                    // get range of variants
                    TypeProvider<?> typeProvider = TypeProviderRegistry.getProvider(currentShapeType.getEntityType());
                    int range = typeProvider != null ? typeProvider.getRange() : -1;
                    // add special shape as extra variant
                    if (hasSpecialVariant) {
                        range++;
                        LivingEntity currentShape = PlayerShape.getCurrentShape(minecraft.player);
                        if (currentShape != null) {
                            CompoundTag nbt = new CompoundTag();
                            currentShape.saveWithoutId(nbt);
                            if (nbt.contains("isSpecial") && nbt.getBoolean("isSpecial")) {
                                currentVariantId = range;
                            }
                        }
                    }

                    // get data of menu
                    int x = minecraft.getWindow().getGuiScaledWidth() / 7;
                    int y = minecraft.getWindow().getGuiScaledHeight() / 5;
                    // render transparent background
                    guiGraphics.fillGradient(x, 0, x * 6, y + 10, -1072689136, -804253680);
                    // render entities
                    if (range > -1) {
                        WalkersClient.variantOffset = Mth.clamp(WalkersClient.variantOffset, -currentVariantId - (hasSpecialVariant ? 1 : 0), range - currentVariantId);
                        for (int i = 1; i <= 5; i++) {
                            int thisVariantId = currentVariantId - 3 + i + WalkersClient.variantOffset;
                            LivingEntity entity = null;
                            // special shape is rendered as an extra variant
                            if (hasSpecialVariant && thisVariantId == range) {
                                entity = renderSpecialEntities.computeIfAbsent(currentShapeType, type -> {
                                    CompoundTag nbt = new CompoundTag();

                                    nbt.putBoolean("isSpecial", true);
                                    nbt.putString("id", EntityType.getKey(type.getEntityType()).toString());
                                    return (LivingEntity) EntityType.loadEntityRecursive(nbt, level, EntitySpawnReason.LOAD, it -> it);
                                });
                            } else if ((thisVariantId > -1 || (hasSpecialVariant && thisVariantId == -1)) && (thisVariantId <= range || thisVariantId == currentVariantId)) {
                                ShapeType<?> thisShapeType = ShapeType.from(currentShapeType.getEntityType(), thisVariantId);
                                if (thisShapeType != null) {
                                    entity = renderEntities.computeIfAbsent(thisShapeType, type -> type.create(level, minecraft.player));
                                }
                            }
                            if (entity != null) {
                                InventoryScreen.renderEntityInInventory(guiGraphics, (float) x * i + (float) x / 2, (float) y * .75f, (int) (25 * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth())))), new Vector3f(), new Quaternionf().rotationXYZ(0.43633232F, (float) Math.PI, (float) Math.PI), null, entity);
                            }
                        }
                    } else {
                        LivingEntity entity = renderEntities.computeIfAbsent(currentShapeType, type -> type.create(level, minecraft.player));
                        if (entity != null) {
                            InventoryScreen.renderEntityInInventory(guiGraphics, (float) x * 3 + (float) x / 2, (float) y * .75f, (int) (25 * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth())))), new Vector3f(), new Quaternionf().rotationXYZ(0.43633232F, (float) Math.PI, (float) Math.PI), null, entity);
                        }
                    }
                    // render focus
                    guiGraphics.blit(RenderType::guiTextured, Walkers.id("textures/gui/focused.png"), x * 3, 5, 0, 0, x, y, x, y);
                }
            }
        }
    }
}
