package tocraft.walkers.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(RenderLayer.class)
public abstract class RenderLayerMixin {
    @Inject(method = "coloredCutoutModelCopyLayerRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;copyPropertiesTo(Lnet/minecraft/client/model/EntityModel;)V", ordinal = 0))
    private static <T extends LivingEntity> void onRenderLayer(EntityModel<T> modelParent, EntityModel<T> model, ResourceLocation textureLocation, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTicks, float red, float green, float blue, CallbackInfo ci) {
        if (modelParent instanceof HumanoidModel<T> humanoidModelParent && model instanceof HumanoidModel<T> humanoidModel) {
            humanoidModel.crouching = humanoidModelParent.crouching;
        }
    }
}
