package tocraft.walkers.mixin.client;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.model.ArmRenderingManipulator;
import tocraft.walkers.api.model.EntityArms;
import tocraft.walkers.api.model.EntityUpdater;
import tocraft.walkers.api.model.EntityUpdaters;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.mixin.accessor.EntityAccessor;
import tocraft.walkers.mixin.accessor.LivingEntityAccessor;
import tocraft.walkers.mixin.client.accessor.LimbAnimatorAccessor;
import tocraft.walkers.mixin.client.accessor.LivingEntityRendererAccessor;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("ALL")
@Environment(EnvType.CLIENT)
@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    @Contract(pure = true)
    @Shadow
    private static HumanoidModel.@Nullable ArmPose getArmPose(AbstractClientPlayer player, InteractionHand hand) {
        return null;
    }

    private PlayerEntityRendererMixin(EntityRendererProvider.Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @WrapWithCondition(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private boolean redirectRender(LivingEntityRenderer<AbstractClientPlayer, EntityModel<AbstractClientPlayer>> renderer, LivingEntity player, float f, float g, PoseStack matrixStack, MultiBufferSource buffer, int i) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) player);

        Optional<UUID> vehiclePlayerId = ((PlayerDataProvider) player).walkers$getVehiclePlayerUUID();
        if (vehiclePlayerId.isPresent()) {
            Vec3 vehiclePos = player.getCommandSenderWorld().getPlayerByUUID(vehiclePlayerId.get()).position();
            player.moveTo(new Vec3(vehiclePos.x, vehiclePos.y + 1, vehiclePos.z));
        }

        // sync player data to shape
        if (shape != null) {
            ((LimbAnimatorAccessor) shape.walkAnimation).setPrevSpeed(((LimbAnimatorAccessor) player.walkAnimation).getPrevSpeed());
            shape.walkAnimation.setSpeed(player.walkAnimation.speed());
            ((LimbAnimatorAccessor) shape.walkAnimation).setPos(player.walkAnimation.position());
            shape.swinging = player.swinging;
            shape.swingTime = player.swingTime;
            shape.oAttackAnim = player.oAttackAnim;
            shape.attackAnim = player.attackAnim;
            shape.yBodyRot = player.yBodyRot;
            shape.yBodyRotO = player.yBodyRotO;
            shape.yHeadRot = player.yHeadRot;
            shape.yHeadRotO = player.yHeadRotO;
            shape.tickCount = player.tickCount;
            shape.swingingArm = player.swingingArm;
            ((LivingEntityAccessor) shape).setSwimAmount(((LivingEntityAccessor) player).getSwimAmount());
            ((LivingEntityAccessor) shape).setSwimAmountO(((LivingEntityAccessor) player).getSwimAmountO());
            shape.setOnGround(player.onGround());
            shape.setDeltaMovement(player.getDeltaMovement());
            shape.setInvisible(player.isInvisibleTo(Minecraft.getInstance().player));

            ((EntityAccessor) shape).setVehicle(player.getVehicle());
            ((EntityAccessor) shape).setPassengers(ImmutableList.copyOf(player.getPassengers()));
            ((EntityAccessor) shape).setTouchingWater(player.isInWater());

            // phantoms' pitch is inverse for whatever reason
            if (shape instanceof Phantom) {
                shape.setXRot(-player.getXRot());
                shape.xRotO = -player.xRotO;
            } else {
                shape.setXRot(player.getXRot());
                shape.xRotO = player.xRotO;
            }

            // equip held items on shape
            if (Walkers.CONFIG.shapesEquipItems) {
                shape.setItemSlot(EquipmentSlot.MAINHAND, player.getItemBySlot(EquipmentSlot.MAINHAND));
                shape.setItemSlot(EquipmentSlot.OFFHAND, player.getItemBySlot(EquipmentSlot.OFFHAND));
            }

            // equip armor items on shape
            if (Walkers.CONFIG.shapesEquipArmor) {
                shape.setItemSlot(EquipmentSlot.HEAD, player.getItemBySlot(EquipmentSlot.HEAD));
                shape.setItemSlot(EquipmentSlot.CHEST, player.getItemBySlot(EquipmentSlot.CHEST));
                shape.setItemSlot(EquipmentSlot.LEGS, player.getItemBySlot(EquipmentSlot.LEGS));
                shape.setItemSlot(EquipmentSlot.FEET, player.getItemBySlot(EquipmentSlot.FEET));
            }

            if (shape instanceof Mob) {
                ((Mob) shape).setAggressive(player.isUsingItem());
            }

            // Assign pose
            shape.setPose(player.getPose());

            // set active hand after configuring held items
            shape.startUsingItem(player.getUsedItemHand() == null ? InteractionHand.MAIN_HAND : player.getUsedItemHand());
            ((LivingEntityAccessor) shape).callSetLivingEntityFlag(1, player.isUsingItem());
            shape.getTicksUsingItem();
            ((LivingEntityAccessor) shape).callUpdatingUsingItem();
            shape.hurtTime = player.hurtTime; // FIX: https://github.com/Draylar/identity/issues/424

            // update shape specific properties
            EntityUpdater<LivingEntity> entityUpdater = EntityUpdaters.getUpdater((EntityType<LivingEntity>) shape.getType());
            if (entityUpdater != null) {
                entityUpdater.update((Player) player, shape);
            }

            if (!player.isInvisibleTo(Minecraft.getInstance().player) && !player.isSpectator()) {
                EntityRenderer<LivingEntity> shapeRenderer = (EntityRenderer<LivingEntity>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(shape);

                // Sync biped information for stuff like bow drawing animation
                if (shapeRenderer instanceof HumanoidMobRenderer) {
                    shape_setBipedShapeModelPose((AbstractClientPlayer) player, shape, (HumanoidMobRenderer<?, ?>) shapeRenderer);
                }

                shapeRenderer.render(shape, f, g, matrixStack, buffer, i);

                // Only render nametags if the server option is true and the entity being
                // rendered is NOT this player/client
                if (Walkers.CONFIG.showPlayerNametag && player != Minecraft.getInstance().player) {
                    renderNameTag((AbstractClientPlayer) player, player.getDisplayName(), matrixStack, buffer, i);
                }
            }

            return false;
        } else {
            return true;
        }
    }

    @Unique
    private void shape_setBipedShapeModelPose(AbstractClientPlayer player, LivingEntity shape, LivingEntityRenderer<?, ?> shapeRenderer) {
        HumanoidModel<?> shapeBipedModel = (HumanoidModel<?>) shapeRenderer.getModel();

        if (shape.isSpectator()) {
            shapeBipedModel.setAllVisible(false);
            shapeBipedModel.head.visible = true;
            shapeBipedModel.hat.visible = true;
        } else {
            shapeBipedModel.setAllVisible(true);
            shapeBipedModel.hat.visible = player.isModelPartShown(PlayerModelPart.HAT);
            shapeBipedModel.crouching = shape.isCrouching();

            HumanoidModel.ArmPose mainHandPose = getArmPose(player, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose offHandPose = getArmPose(player, InteractionHand.OFF_HAND);

            if (mainHandPose != null && mainHandPose.isTwoHanded()) {
                offHandPose = shape.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }

            if ((mainHandPose != null && offHandPose != null) && shape.getMainArm() == HumanoidArm.RIGHT) {
                shapeBipedModel.rightArmPose = mainHandPose;
                shapeBipedModel.leftArmPose = offHandPose;
            } else if (mainHandPose != null && offHandPose != null) {
                shapeBipedModel.rightArmPose = offHandPose;
                shapeBipedModel.leftArmPose = mainHandPose;
            }
        }
    }

    @Inject(method = "getRenderOffset(Lnet/minecraft/client/player/AbstractClientPlayer;F)Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
    private void modifyPositionOffset(AbstractClientPlayer player, float f, CallbackInfoReturnable<Vec3> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (shape != null) {
            if (shape instanceof TamableAnimal) {
                cir.setReturnValue(super.getRenderOffset(player, f));
            }
        }
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void onRenderArm(PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        // sync player data to shape
        if (shape != null) {
            EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(shape);

            if (renderer instanceof LivingEntityRenderer) {
                LivingEntityRenderer<LivingEntity, ?> rendererCasted = (LivingEntityRenderer<LivingEntity, ?>) renderer;
                EntityModel model = ((LivingEntityRenderer<?, ?>) renderer).getModel();

                // re-assign arm & sleeve models
                arm = null;
                sleeve = null;

                if (model instanceof PlayerModel) {
                    arm = ((PlayerModel<?>) model).rightArm;
                    sleeve = ((PlayerModel<?>) model).rightSleeve;
                } else if (model instanceof HumanoidModel) {
                    arm = ((HumanoidModel<?>) model).rightArm;
                    sleeve = null;
                } else {
                    Tuple<ModelPart, ArmRenderingManipulator<EntityModel<Entity>>> pair = EntityArms.get(shape, model);
                    if (pair != null) {
                        arm = pair.getA();
                        pair.getB().run(matrices, model);
                        matrices.translate(0, -.35, .5);
                    }
                }

                // assign model properties
                model.attackTime = 0.0F;
//                model.sneaking = false;
//                model.leaningPitch = 0.0F;
                model.setupAnim(shape, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

                // render
                if (arm != null) {
                    arm.xRot = 0.0F;
                    arm.render(matrices, vertexConsumers.getBuffer(((LivingEntityRendererAccessor) rendererCasted).callGetRenderType(shape, true, false, true)), light, OverlayTexture.NO_OVERLAY);
                }

                if (sleeve != null) {
                    sleeve.xRot = 0.0F;
                    sleeve.render(matrices, vertexConsumers.getBuffer(((LivingEntityRendererAccessor) rendererCasted).callGetRenderType(shape, true, false, true)), light, OverlayTexture.NO_OVERLAY);
                }

                ci.cancel();
            }
        }
    }
}
