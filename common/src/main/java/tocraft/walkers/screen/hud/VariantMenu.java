package tocraft.walkers.screen.hud;

import dev.tocraft.craftedcore.event.client.RenderEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.ApiStatus;
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

@Environment(EnvType.CLIENT)
public class VariantMenu implements RenderEvents.HUDRendering {
    private static final Map<ShapeType<?>, LivingEntity> renderEntities = new HashMap<>();
    private static final Map<ShapeType<?>, LivingEntity> renderSpecialEntities = new HashMap<>();

    // reset cached entities so e.g. biome based variants are rendered correctly
    @ApiStatus.Internal
    public static void clearEntities() {
        renderEntities.clear();
        renderSpecialEntities.clear();
    }

    public void render(GuiGraphics guiGraphics, DeltaTracker delta) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.hideGui && WalkersClient.isRenderingVariantsMenu && Walkers.CONFIG.unlockEveryVariant && minecraft.screen == null) {
            Level level = minecraft.level;
            if (level != null && minecraft.player != null) {
                ShapeType<?> currentShapeType = ShapeType.from(PlayerShape.getCurrentShape(minecraft.player));
                if (currentShapeType != null) {
                    boolean hasSpecialVariant = Walkers.hasSpecialShape(minecraft.player.getUUID()) && EntityType.getKey(currentShapeType.getEntityType()).equals(ResourceLocation.parse("minecraft:wolf"));

                    int currVariant = currentShapeType.getVariantData();

                    // get range of variants
                    TypeProvider<?> typeProvider = TypeProviderRegistry.getProvider(currentShapeType.getEntityType());
                    int range = typeProvider != null ? typeProvider.size(minecraft.level) : -1;
                    // add special shape as extra variant
                    if (hasSpecialVariant) {
                        LivingEntity currentShape = PlayerShape.getCurrentShape(minecraft.player);
                        if (currentShape != null) {
                            TagValueOutput out = TagValueOutput.createWithContext(Walkers.PROBLEM_REPORTER, level.registryAccess());
                            currentShape.saveWithoutId(out);
                            CompoundTag nbt = out.buildResult();
                            if (nbt.contains("isSpecial") && nbt.getBoolean("isSpecial").orElse(false)) {
                                currVariant = range;
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
                        WalkersClient.variantOffset = Mth.clamp(WalkersClient.variantOffset, -currVariant - (hasSpecialVariant ? 1 : 0), range - currVariant - (hasSpecialVariant ? 0 : 1));
                        for (int i = 1; i <= 5; i++) {
                            int variant = currVariant - 3 + i + WalkersClient.variantOffset;
                            LivingEntity entity = null;
                            // special shape is rendered as an extra variant
                            if (hasSpecialVariant && variant == range) {
                                entity = renderSpecialEntities.computeIfAbsent(currentShapeType, type -> {
                                    CompoundTag nbt = new CompoundTag();

                                    nbt.putBoolean("isSpecial", true);
                                    nbt.putString("id", EntityType.getKey(type.getEntityType()).toString());
                                    return (LivingEntity) EntityType.loadEntityRecursive(nbt, level, EntitySpawnReason.LOAD, it -> it);
                                });
                            } else if ((variant > -1 || (hasSpecialVariant && variant == -1)) && (variant < range || variant == currVariant)) {
                                ShapeType<?> thisShapeType = ShapeType.from(currentShapeType.getEntityType(), variant);
                                if (thisShapeType != null) {
                                    entity = renderEntities.computeIfAbsent(thisShapeType, type -> type.create(level, minecraft.player));
                                }
                            }
                            if (entity != null) {
                                guiGraphics.pose().pushMatrix();
                                int leftPos = (int) ((float) x * i + (float) x / 2);
                                int topPos = (int) ((float) y * .75f);
                                int k = leftPos - 20;
                                int l = topPos - 30;
                                int m = leftPos + 20;
                                int n = topPos + 30;
                                InventoryScreen.renderEntityInInventory(guiGraphics, k, l, m, n, (int) (25 / (Math.max(entity.getBbHeight(), entity.getBbWidth()))), new Vector3f(), new Quaternionf().rotationXYZ(0.43633232F, (float) Math.PI, (float) Math.PI), null, entity);
                                guiGraphics.pose().popMatrix();
                            }
                        }
                    } else {
                        LivingEntity entity = renderEntities.computeIfAbsent(currentShapeType, type -> type.create(level, minecraft.player));
                        if (entity != null) {
                            guiGraphics.pose().pushMatrix();
                            int leftPos = (int) ((float) x * 3 + (float) x / 2);
                            int topPos = (int) ((float) y * .75f);
                            int k = leftPos - 20;
                            int l = topPos - 30;
                            int m = leftPos + 20;
                            int n = topPos + 30;
                            InventoryScreen.renderEntityInInventory(guiGraphics, k, l, m, n, (int) (25 / (Math.max(entity.getBbHeight(), entity.getBbWidth()))), new Vector3f(), new Quaternionf().rotationXYZ(0.43633232F, (float) Math.PI, (float) Math.PI), null, entity);
                            guiGraphics.pose().popMatrix();
                        }
                    }
                    // render focus
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Walkers.id("textures/gui/focused.png"), x * 3, 5, 0, 0, x, y, x, y);
                }
            }
        }
    }

    /*@Override
    public void render(GuiGraphics graphics, DeltaTracker tickCounter) {
        LivingEntity wolf = EntityType.WOLF.create(Minecraft.getInstance().level, EntitySpawnReason.LOAD);
        LivingEntity cat = EntityType.CAT.create(Minecraft.getInstance().level, EntitySpawnReason.LOAD);

        int x = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 7;
        int y = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 5;

        graphics.pose().pushMatrix();
        if (wolf != null) {
            int leftPos = (int) ((float) x * 4 + (float) x / 2);
            int topPos = (int) ((float) y * .75f);
            int k = leftPos - 20;
            int l = topPos - 30;
            int m = leftPos + 20;
            int n = topPos + 30;
            InventoryScreen.renderEntityInInventory(graphics, k, l, m, n, (int) (25 / (Math.max(wolf.getBbHeight(), wolf.getBbWidth()))), new Vector3f(), new Quaternionf().rotationXYZ(0.43633232F, (float) Math.PI, (float) Math.PI), null, wolf);
            InventoryScreen.renderEntityInInventory();
        }

        if (cat != null) {
            int leftPos = (int) ((float) x * 2 + (float) x / 2);
            int topPos = (int) ((float) y * .75f);
            int k = leftPos - 20;
            int l = topPos - 30;
            int m = leftPos + 20;
            int n = topPos + 30;
            InventoryScreen.renderEntityInInventory(graphics, k, l, m, n, (int) (25 / (Math.max(cat.getBbHeight(), cat.getBbWidth()))), new Vector3f(), new Quaternionf().rotationXYZ(0.43633232F, (float) Math.PI, (float) Math.PI), null, cat);
        }
        graphics.pose().popMatrix();
    }*/
}
