package tocraft.walkers.screen.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.api.variant.TypeProviderRegistry;

public class VariantMenu {
    public void render(PoseStack matrices) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.hideGui && WalkersClient.isRenderingVariantsMenu && Walkers.CONFIG.unlockEveryVariant && minecraft.screen == null) {
            Level level = minecraft.level;
            if (level != null && minecraft.player != null) {
                ShapeType<?> currentShapeType = ShapeType.from(PlayerShape.getCurrentShape(minecraft.player));
                if (currentShapeType != null) {
                    boolean hasSpecialVariant = Walkers.hasSpecialShape(minecraft.player.getUUID()) && Registry.ENTITY_TYPE.getKey(currentShapeType.getEntityType()).equals(new ResourceLocation("minecraft:wolf"));

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
                    BackportedGraphicTools.fillGradient(matrices, x, 0, x * 6, y + 10, -1072689136, -804253680);
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
                                nbt.putString("id", Registry.ENTITY_TYPE.getKey(currentShapeType.getEntityType()).toString());
                                entity = (LivingEntity) EntityType.loadEntityRecursive(nbt, level, it -> it);
                            } else if ((thisVariantId > -1 || (hasSpecialVariant && thisVariantId == -1)) && (thisVariantId <= range || thisVariantId == currentVariantId)) {
                                ShapeType<?> thisShapeType = ShapeType.from(currentShapeType.getEntityType(), thisVariantId);
                                if (thisShapeType != null) {
                                    entity = thisShapeType.create(level, minecraft.player);
                                }
                            }
                            if (entity != null) {
                                InventoryScreen.renderEntityInInventory((int) ((float) x * i + (float) x / 2), (int) ((float) y * .75f), (int) (25 * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth())))), -10, -10, entity);
                            }
                        }
                    } else {
                        LivingEntity entity = currentShapeType.create(level);
                        if (entity != null) {
                            InventoryScreen.renderEntityInInventory((int) ((float) x * 3 + (float) x / 2), (int) ((float) y * .75f), (int) (25 * (1 / (Math.max(entity.getBbHeight(), entity.getBbWidth())))), -10, -10, entity);
                        }
                    }
                    // render focus
                    RenderSystem.setShaderTexture(0, Walkers.id("textures/gui/focused.png"));
                    GuiComponent.blit(matrices, x * 3, 5, x, y, 0, 0, 48, 32, 48, 32);
                }
            }
        }
    }

    private static class BackportedGraphicTools {
        protected static void fillGradient(PoseStack poseStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
            fillGradient(poseStack, x1, y1, x2, y2, colorFrom, colorTo, 0);
        }

        protected static void fillGradient(PoseStack poseStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo, int blitOffset) {
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            fillGradient(poseStack.last().pose(), bufferBuilder, x1, y1, x2, y2, blitOffset, colorFrom, colorTo);
            tesselator.end();
            RenderSystem.disableBlend();
        }

        protected static void fillGradient(Matrix4f matrix, BufferBuilder builder, int x1, int y1, int x2, int y2, int blitOffset, int colorA, int colorB) {
            float f = (float) FastColor.ARGB32.alpha(colorA) / 255.0F;
            float g = (float) FastColor.ARGB32.red(colorA) / 255.0F;
            float h = (float) FastColor.ARGB32.green(colorA) / 255.0F;
            float i = (float) FastColor.ARGB32.blue(colorA) / 255.0F;
            float j = (float) FastColor.ARGB32.alpha(colorB) / 255.0F;
            float k = (float) FastColor.ARGB32.red(colorB) / 255.0F;
            float l = (float) FastColor.ARGB32.green(colorB) / 255.0F;
            float m = (float) FastColor.ARGB32.blue(colorB) / 255.0F;
            builder.vertex(matrix, (float) x1, (float) y1, (float) blitOffset).color(g, h, i, f).endVertex();
            builder.vertex(matrix, (float) x1, (float) y2, (float) blitOffset).color(k, l, m, j).endVertex();
            builder.vertex(matrix, (float) x2, (float) y2, (float) blitOffset).color(k, l, m, j).endVertex();
            builder.vertex(matrix, (float) x2, (float) y1, (float) blitOffset).color(g, h, i, f).endVertex();
        }
    }
}
