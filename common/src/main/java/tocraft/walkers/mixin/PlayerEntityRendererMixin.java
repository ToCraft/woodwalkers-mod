package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.model.ArmRenderingManipulator;
import tocraft.walkers.api.model.EntityArms;
import tocraft.walkers.api.model.EntityUpdater;
import tocraft.walkers.api.model.EntityUpdaters;
import tocraft.walkers.api.platform.SyncedVars;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.mixin.accessor.EntityAccessor;
import tocraft.walkers.mixin.accessor.LimbAnimatorAccessor;
import tocraft.walkers.mixin.accessor.LivingEntityAccessor;
import tocraft.walkers.mixin.accessor.LivingEntityRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    @Shadow
    protected static BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity player, Hand hand) {
        return null;
    }

    private PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    )
    private void redirectRender(LivingEntityRenderer renderer, LivingEntity player, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        LivingEntity shape = PlayerShape.getCurrentShape((PlayerEntity) player);

        // sync player data to shape
        if(shape != null) {
            ((LimbAnimatorAccessor)shape.limbAnimator).setPrevSpeed(((LimbAnimatorAccessor)player.limbAnimator).getPrevSpeed());
            shape.limbAnimator.setSpeed(player.limbAnimator.getSpeed());
            ((LimbAnimatorAccessor)shape.limbAnimator).setPos(player.limbAnimator.getPos());
            shape.handSwinging = player.handSwinging;
            shape.handSwingTicks = player.handSwingTicks;
            shape.lastHandSwingProgress = player.lastHandSwingProgress;
            shape.handSwingProgress = player.handSwingProgress;
            shape.bodyYaw = player.bodyYaw;
            shape.prevBodyYaw = player.prevBodyYaw;
            shape.headYaw = player.headYaw;
            shape.prevHeadYaw = player.prevHeadYaw;
            shape.age = player.age;
            shape.preferredHand = player.preferredHand;
            shape.setOnGround(player.isOnGround());
            shape.setVelocity(player.getVelocity());

            ((EntityAccessor) shape).setVehicle(player.getVehicle());
            ((EntityAccessor) shape).setTouchingWater(player.isTouchingWater());

            // phantoms' pitch is inverse for whatever reason
            if(shape instanceof PhantomEntity) {
                shape.setPitch(-player.getPitch());
                shape.prevPitch = -player.prevPitch;
            } else {
                shape.setPitch(player.getPitch());
                shape.prevPitch = player.prevPitch;
            }

            // equip held items on shape
            if(WalkersConfig.getInstance().shapesEquipItems()) {
                shape.equipStack(EquipmentSlot.MAINHAND, player.getEquippedStack(EquipmentSlot.MAINHAND));
                shape.equipStack(EquipmentSlot.OFFHAND, player.getEquippedStack(EquipmentSlot.OFFHAND));
            }

            // equip armor items on shape
            if(WalkersConfig.getInstance().shapesEquipArmor()) {
                shape.equipStack(EquipmentSlot.HEAD, player.getEquippedStack(EquipmentSlot.HEAD));
                shape.equipStack(EquipmentSlot.CHEST, player.getEquippedStack(EquipmentSlot.CHEST));
                shape.equipStack(EquipmentSlot.LEGS, player.getEquippedStack(EquipmentSlot.LEGS));
                shape.equipStack(EquipmentSlot.FEET, player.getEquippedStack(EquipmentSlot.FEET));
            }

            if (shape instanceof MobEntity) {
                ((MobEntity) shape).setAttacking(player.isUsingItem());
            }

            // Assign pose
            shape.setPose(player.getPose());

            // set active hand after configuring held items
            shape.setCurrentHand(player.getActiveHand() == null ? Hand.MAIN_HAND : player.getActiveHand());
            ((LivingEntityAccessor) shape).callSetLivingFlag(1, player.isUsingItem());
            shape.getItemUseTime();
            ((LivingEntityAccessor) shape).callTickActiveItemStack();
            shape.hurtTime = player.hurtTime; // FIX: https://github.com/Draylar/identity/issues/424

            // update shape specific properties
            EntityUpdater entityUpdater = EntityUpdaters.getUpdater((EntityType<? extends LivingEntity>) shape.getType());
            if(entityUpdater != null) {
                entityUpdater.update((PlayerEntity) player, shape);
            }
        }

        if(shape != null) {
            EntityRenderer shapeRenderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(shape);

            // Sync biped information for stuff like bow drawing animation
            if(shapeRenderer instanceof BipedEntityRenderer) {
                shape_setBipedShapeModelPose((AbstractClientPlayerEntity) player, shape, (BipedEntityRenderer) shapeRenderer);
            }

            shapeRenderer.render(shape, f, g, matrixStack, vertexConsumerProvider, i);

            // Only render nametags if the server option is true and the entity being rendered is NOT this player/client
            if(SyncedVars.getShowPlayerNametag() && player != MinecraftClient.getInstance().player) {
                renderLabelIfPresent((AbstractClientPlayerEntity) player, player.getDisplayName(), matrixStack, vertexConsumerProvider, i);
            }
        } else {
            super.render((AbstractClientPlayerEntity) player, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }

    private void shape_setBipedShapeModelPose(AbstractClientPlayerEntity player, LivingEntity shape, LivingEntityRenderer shapeRenderer) {
        BipedEntityModel<?> shapeBipedModel = (BipedEntityModel) shapeRenderer.getModel();

        if (shape.isSpectator()) {
            shapeBipedModel.setVisible(false);
            shapeBipedModel.head.visible = true;
            shapeBipedModel.hat.visible = true;
        } else {
            shapeBipedModel.setVisible(true);
            shapeBipedModel.hat.visible = player.isPartVisible(PlayerModelPart.HAT);
            shapeBipedModel.sneaking = shape.isInSneakingPose();

            BipedEntityModel.ArmPose mainHandPose = getArmPose(player, Hand.MAIN_HAND);
            BipedEntityModel.ArmPose offHandPose = getArmPose(player, Hand.OFF_HAND);

            if (mainHandPose.isTwoHanded()) {
                offHandPose = shape.getOffHandStack().isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
            }

            if (shape.getMainArm() == Arm.RIGHT) {
                shapeBipedModel.rightArmPose = mainHandPose;
                shapeBipedModel.leftArmPose = offHandPose;
            } else {
                shapeBipedModel.rightArmPose = offHandPose;
                shapeBipedModel.leftArmPose = mainHandPose;
            }
        }
    }

    @Inject(
            method = "getPositionOffset",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyPositionOffset(AbstractClientPlayerEntity player, float f, CallbackInfoReturnable<Vec3d> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if(shape != null) {
            if(shape instanceof TameableEntity) {
                cir.setReturnValue(super.getPositionOffset(player, f));
            }
        }
    }

    @Inject(
            method = "renderArm",
            at = @At("HEAD"), cancellable = true)
    private void onRenderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        // sync player data to shape
        if(shape != null) {
            EntityRenderer<?> renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(shape);

            if(renderer instanceof LivingEntityRenderer) {
                LivingEntityRenderer<LivingEntity, ?> rendererCasted = (LivingEntityRenderer<LivingEntity, ?>) renderer;
                EntityModel model = ((LivingEntityRenderer) renderer).getModel();

                // re-assign arm & sleeve models
                arm = null;
                sleeve = null;

                if(model instanceof PlayerEntityModel) {
                    arm = ((PlayerEntityModel) model).rightArm;
                    sleeve = ((PlayerEntityModel) model).rightSleeve;
                } else if(model instanceof BipedEntityModel) {
                    arm = ((BipedEntityModel) model).rightArm;
                    sleeve = null;
                } else {
                    Pair<ModelPart, ArmRenderingManipulator<EntityModel>> pair = EntityArms.get(shape, model);
                    if(pair != null) {
                        arm = pair.getLeft();
                        pair.getRight().run(matrices, model);
                        matrices.translate(0, -.35, .5);
                    }
                }

                // assign model properties
                model.handSwingProgress = 0.0F;
//                model.sneaking = false;
//                model.leaningPitch = 0.0F;
                model.setAngles(shape, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

                // render
                if(arm != null) {
                    arm.pitch = 0.0F;
                    arm.render(matrices, vertexConsumers.getBuffer(((LivingEntityRendererAccessor) rendererCasted).callGetRenderLayer(shape, true, false, true)), light, OverlayTexture.DEFAULT_UV);
                }

                if(sleeve != null) {
                    sleeve.pitch = 0.0F;
                    sleeve.render(matrices, vertexConsumers.getBuffer(((LivingEntityRendererAccessor) rendererCasted).callGetRenderLayer(shape, true, false, true)), light, OverlayTexture.DEFAULT_UV);
                }

                ci.cancel();
            }
        }
    }
}
