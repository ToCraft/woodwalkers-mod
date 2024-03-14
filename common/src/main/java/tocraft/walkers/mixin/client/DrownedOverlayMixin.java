package tocraft.walkers.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.monster.Drowned;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(DrownedOuterLayer.class)
public abstract class DrownedOverlayMixin extends RenderLayer<Drowned, DrownedModel<Drowned>> {

    @Shadow
    @Final
    private DrownedModel<Drowned> model;

    public DrownedOverlayMixin(RenderLayerParent<Drowned, DrownedModel<Drowned>> context) {
        super(context);
    }

    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/monster/Drowned;FFFFFF)V",
            at = @At("HEAD"))
    private void onRender(PoseStack matrixStack, MultiBufferSource buffer, int i, Drowned drownedEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        this.model.crouching = drownedEntity.isShiftKeyDown();
    }
}
