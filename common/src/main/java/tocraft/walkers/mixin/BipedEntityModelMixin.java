package tocraft.walkers.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobRenderer.class)
public abstract class BipedEntityModelMixin extends LivingEntityRenderer {

    private BipedEntityModelMixin(EntityRendererProvider.Context ctx, EntityModel<?> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(
            method = "render",
            at = @At("HEAD"))
    private void onRender(Mob mobEntity, float f, float g, PoseStack matrixStack, MultiBufferSource buffer, int i, CallbackInfo ci) {
        // Only apply to Biped Entities
        if (!((Object) this instanceof HumanoidMobRenderer)) {
            return;
        }

        HumanoidModel<?> model = (HumanoidModel<?>) getModel();

        if (model != null) {
            model.crouching = mobEntity.isCrouching();
        }
    }
}
