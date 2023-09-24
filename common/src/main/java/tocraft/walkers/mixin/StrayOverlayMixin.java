package tocraft.walkers.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.StrayClothingLayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Stray;

@Mixin(StrayClothingLayer.class)
public abstract class StrayOverlayMixin<T extends Mob & RangedAttackMob, M extends EntityModel<T>>
		extends RenderLayer<T, M> {

	@Shadow
	@Final
	private SkeletonModel<Stray> layerModel;

	public StrayOverlayMixin(RenderLayerParent<T, M> context) {
		super(context);
	}

	@Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/Mob;FFFFFF)V", at = @At("HEAD"))
	private void onRender(PoseStack matrixStack, MultiBufferSource buffer, int i, T mobEntity, float f,
			float g, float h, float j, float k, float l, CallbackInfo ci) {
		M layerModel = getParentModel();

		if (layerModel instanceof HumanoidModel) {
			this.layerModel.copyPropertiesTo((HumanoidModel<Stray>) layerModel);
			((HumanoidModel) layerModel).crouching = mobEntity.isCrouching();
		}
	}
}
