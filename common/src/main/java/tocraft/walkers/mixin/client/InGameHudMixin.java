package tocraft.walkers.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.api.variant.TypeProviderRegistry;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.UndrownableSkill;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class InGameHudMixin {

    @Shadow
    protected abstract Player getCameraPlayer();

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private int screenWidth;

    @Shadow
    private int screenHeight;

    @ModifyArg(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
    private TagKey<Fluid> shouldRenderBreath(TagKey<Fluid> tag) {
        Player player = this.getCameraPlayer();
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (shape != null) {
            if (Walkers.isAquatic(shape) < 2 || SkillRegistry.has(shape, UndrownableSkill.ID) && player.isEyeInFluid(FluidTags.WATER)) {
                return FluidTags.LAVA; // will cause isEyeInFluid to return false, preventing air render
            }
        }

        return tag;
    }

    @Unique
    private final Map<ShapeType<?>, LivingEntity> walkers$renderedEntities = new HashMap<>();

    @Inject(method = "render", at = @At("RETURN"))
    private void renderVariantMenu(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if (!minecraft.options.hideGui && WalkersClient.renderVariantsMenu && Walkers.CONFIG.unlockEveryVariant && minecraft.screen == null) {
            Level level = minecraft.level;
            if (level != null && minecraft.player != null) {
                ShapeType<?> currentShapeType = ShapeType.from(((PlayerDataProvider) minecraft.player).walkers$getCurrentShape());
                if (currentShapeType != null) {
                    int currentVariantId = currentShapeType.getVariantData();

                    // get data of menu
                    int x = this.screenWidth / 7;
                    int y = this.screenHeight / 5;
                    // render transparent background
                    guiGraphics.fillGradient(x, 0, x * 6, y + 10, -1072689136, -804253680);
                    // render entities
                    TypeProvider<?> typeProvider = TypeProviderRegistry.getProvider(currentShapeType.getEntityType());
                    if (typeProvider != null) {
                        if (WalkersClient.variantOffset < -currentVariantId) WalkersClient.variantOffset = -currentVariantId;
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
