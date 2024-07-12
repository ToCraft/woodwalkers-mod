package tocraft.walkers.screen.hud;

import net.minecraft.client.Minecraft;
//#if MC>1194
import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
//#if MC>1182
import org.joml.Quaternionf;
//#else
//$$ import com.mojang.math.Quaternion;
//#endif
//#if MC>1201
import org.joml.Vector3f;
//#endif
import tocraft.craftedcore.patched.Identifier;
import tocraft.craftedcore.patched.client.CGraphics;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.api.variant.TypeProviderRegistry;

public class VariantMenu {
    //#if MC>1194
    public void render(GuiGraphics guiGraphics) {
        //#else
        //$$ public void render(PoseStack guiGraphics) {
        //#endif
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.hideGui && WalkersClient.isRenderingVariantsMenu && Walkers.CONFIG.unlockEveryVariant && minecraft.screen == null) {
            Level level = minecraft.level;
            if (level != null && minecraft.player != null) {
                ShapeType<?> currentShapeType = ShapeType.from(PlayerShape.getCurrentShape(minecraft.player));
                if (currentShapeType != null) {
                    boolean hasSpecialVariant = Walkers.hasSpecialShape(minecraft.player.getUUID()) && Walkers.getEntityTypeRegistry().getKey(currentShapeType.getEntityType()).equals(Identifier.parse("minecraft:wolf"));

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
                    CGraphics.fillTransparent(guiGraphics, x, 0, x * 6, y + 10);
                    // render entities
                    if (range > -1) {
                        WalkersClient.variantOffset = Mth.clamp(WalkersClient.variantOffset, -currentVariantId - (hasSpecialVariant ? 1 : 0), range - currentVariantId);
                        for (int i = 1; i <= 5; i++) {
                            int thisVariantId = currentVariantId - 3 + i + WalkersClient.variantOffset;
                            LivingEntity entity = null;
                            // special shape is rendered as an extra variant
                            if (hasSpecialVariant && thisVariantId == range) {
                                CompoundTag nbt = new CompoundTag();

                                nbt.putBoolean("isSpecial", true);
                                nbt.putString("id", Walkers.getEntityTypeRegistry().getKey(currentShapeType.getEntityType()).toString());
                                entity = (LivingEntity) EntityType.loadEntityRecursive(nbt, level, it -> it);
                            } else if ((thisVariantId > -1 || (hasSpecialVariant && thisVariantId == -1)) && (thisVariantId <= range || thisVariantId == currentVariantId)) {
                                ShapeType<?> thisShapeType = ShapeType.from(currentShapeType.getEntityType(), thisVariantId);
                                if (thisShapeType != null) {
                                    entity = thisShapeType.create(level, minecraft.player);
                                }
                            }
                            if (entity != null) {
                                //#if MC>1201
                                InventoryScreen.renderEntityInInventory(guiGraphics, (float) x * i + (float) x / 2, (float) y * .75f, (int) (25 * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth())))), new Vector3f(), new Quaternionf().rotationXYZ(0.43633232F, (float) Math.PI, (float) Math.PI), null, entity);
                                //#elseif MC>1182
                                //$$ InventoryScreen.renderEntityInInventory(guiGraphics, (int) ((float) x * i + (float) x / 2), (int) ((float) y * .75f), (int) (25 * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth())))), new Quaternionf().rotationXYZ(0.43633232F, (float) Math.PI, (float) Math.PI), null, entity);
                                //#else
                                //$$ InventoryScreen.renderEntityInInventory((int) ((float) x * i + (float) x / 2), (int) ((float) y * .75f), (int) (25 * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth())))), -10, -10, entity);
                                //#endif
                            }
                        }
                    } else {
                        LivingEntity entity = currentShapeType.create(level);
                        if (entity != null) {
                            //#if MC>1201
                            InventoryScreen.renderEntityInInventory(guiGraphics, (float) x * 3 + (float) x / 2, (float) y * .75f, (int) (25 * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth())))), new Vector3f(), new Quaternionf().rotationXYZ(0.43633232F, (float) Math.PI, (float) Math.PI), null, entity);
                            //#elseif MC>1182
                            //$$ InventoryScreen.renderEntityInInventory(guiGraphics, (int) ((float) x * 3 + (float) x / 2), (int) ((float) y * .75f), (int) (25 * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth())))), new Quaternionf().rotationXYZ(0.43633232F, (float) Math.PI, (float) Math.PI), null, entity);
                            //#else
                            //$$ InventoryScreen.renderEntityInInventory((int) ((float) x * 3 + (float) x / 2), (int) ((float) y * .75f), (int) (25 * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth())))), -10, -10, entity);
                            //#endif
                        }
                    }
                    // render focus
                    CGraphics.blit(guiGraphics, Walkers.id("textures/gui/focused.png"), x * 3, 5, x, y, 0, 0, 48, 32, 48, 32);
                }
            }
        }
    }
}
