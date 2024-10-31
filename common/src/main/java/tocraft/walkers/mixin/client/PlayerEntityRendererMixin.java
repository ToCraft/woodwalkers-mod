package tocraft.walkers.mixin.client;

import com.google.common.collect.ImmutableList;
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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.craftedcore.util.Maths;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.model.ArmRenderingManipulator;
import tocraft.walkers.api.model.EntityArms;
import tocraft.walkers.api.model.EntityUpdater;
import tocraft.walkers.api.model.EntityUpdaters;
import tocraft.walkers.impl.ShapeRenderStateProvider;
import tocraft.walkers.mixin.accessor.EntityAccessor;
import tocraft.walkers.mixin.accessor.LivingEntityAccessor;
import tocraft.walkers.mixin.client.accessor.LimbAnimatorAccessor;

@SuppressWarnings({"rawtypes", "unchecked"})
@Environment(EnvType.CLIENT)
@Mixin(PlayerRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {
    private PlayerEntityRendererMixin(EntityRendererProvider.Context ctx, PlayerModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("RETURN"))
    private void onCreateState(AbstractClientPlayer player, PlayerRenderState state, float f, CallbackInfo ci) {
        ((ShapeRenderStateProvider) state).walkers$setShape(() -> {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() || player.getVehicle() != Minecraft.getInstance().cameraEntity) {
                if (shape != null) {
                    walkers$updateShapeAttributes(player, shape);
                }
                return shape;
            }
            return null;
        });
    }

    @Unique
    private void walkers$updateShapeAttributes(@NotNull PlayerRenderState player, @NotNull EntityRenderState shape) {
        shape.y = player.y;
        shape.x = player.x;
        shape.z = player.z;
        shape.ageInTicks = player.ageInTicks;
        shape.passengerOffset = player.passengerOffset;
        shape.leashState = player.leashState;

        if (shape instanceof LivingEntityRenderState livingState) {
            livingState.pose = player.pose;
            livingState.walkAnimationPos = player.walkAnimationPos;
            livingState.walkAnimationSpeed = player.walkAnimationSpeed;
            livingState.bodyRot = player.bodyRot;
            livingState.xRot = player.xRot;
            livingState.yRot = player.yRot;
            livingState.wornHeadAnimationPos = player.wornHeadAnimationPos;
            livingState.isInWater = player.isInWater;
            livingState.mainArm = player.mainArm;

            if (shape instanceof HumanoidRenderState humanoidShape) {
                humanoidShape.swimAmount = player.swimAmount;
                humanoidShape.isVisuallySwimming = player.isVisuallySwimming;
                humanoidShape.attackArm = player.attackArm;
                humanoidShape.attackTime = player.attackTime;
                humanoidShape.speedValue = player.speedValue;
                humanoidShape.isCrouching = player.isCrouching;
                humanoidShape.ticksUsingItem = player.ticksUsingItem;
                humanoidShape.chestItem = player.chestItem;
                humanoidShape.isUsingItem = player.isUsingItem;
                humanoidShape.legsItem = player.legsItem;
                humanoidShape.feetItem = player.feetItem;
            }
        }
    }

    @Unique
    private void walkers$updateShapeAttributes(@NotNull AbstractClientPlayer player, @NotNull LivingEntity shape) {
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
        if (Minecraft.getInstance().player != null) {
            shape.setInvisible(player.isInvisibleTo(Minecraft.getInstance().player));
        }

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
            entityUpdater.update(player, shape);
        }

        // Only render nametags if the server option is true and the entity being
        // rendered is NOT this player/client
        if (Walkers.CONFIG.showPlayerNametag && player != Minecraft.getInstance().player) {
            shape.setCustomName(player.getCustomName());
        }
    }

    @Override
    public void render(PlayerRenderState state, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        LivingEntity shape = ((ShapeRenderStateProvider) state).walkers$getShape();

        // sync player data to shape
        if (shape != null && !state.isSpectator) {
            if (!state.isInvisibleToPlayer && !state.isInvisible) {
                EntityRenderer<LivingEntity, EntityRenderState> shapeRenderer = (EntityRenderer<LivingEntity, EntityRenderState>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(shape);

                EntityRenderState shapeState = shapeRenderer.createRenderState(shape, packedLight);
                walkers$updateShapeAttributes(state, shapeState);

                shapeRenderer.render(shapeState, matrixStack, buffer, packedLight);

            }

            return;
        }
        super.render(state, matrixStack, buffer, packedLight);

    }

    @Inject(method = "getRenderOffset(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;)Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
    private void modifyPositionOffset(PlayerRenderState state, CallbackInfoReturnable<Vec3> cir) {
        LivingEntity shape = ((ShapeRenderStateProvider) state).walkers$getShape();
        if (shape != null) {
            if (shape instanceof TamableAnimal) {
                cir.setReturnValue(super.getRenderOffset(state));
            }
        }
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void onRenderArm(PoseStack matrices, MultiBufferSource vertexConsumers, int light, ResourceLocation resourceLocation, ModelPart arm, boolean bl, CallbackInfo ci) {
        if (Minecraft.getInstance().cameraEntity instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            // sync player data to shape
            if (shape != null) {
                EntityRenderer<LivingEntity, ?> renderer = (EntityRenderer<LivingEntity, ?>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(shape);

                if (renderer instanceof LivingEntityRenderer livingRenderer) {
                    LivingEntityRenderState shapeState = ((LivingEntityRenderer<LivingEntity, ?, ?>) livingRenderer).createRenderState(shape, light);
                    ResourceLocation texture = livingRenderer.getTextureLocation(shapeState);
                    EntityModel model = livingRenderer.getModel();

                    // re-assign arm & sleeve models
                    arm = null;

                    if (model instanceof HumanoidModel) {
                        if (player.getMainArm() == HumanoidArm.RIGHT) {
                            arm = ((HumanoidModel<?>) model).rightArm;
                        } else {
                            arm = ((HumanoidModel<?>) model).leftArm;
                        }
                    } else {
                        Tuple<ModelPart, ArmRenderingManipulator<EntityModel<EntityRenderState>>> pair = EntityArms.get(shape, model);
                        if (pair != null) {
                            arm = pair.getA();
                            // mirror matrices with player is left-handed
                            if (player.getMainArm() == HumanoidArm.LEFT) {
                                matrices.mulPose(Maths.getDegreesQuaternion(Maths.POSITIVE_Y(), 180));
                            }
                            pair.getB().run(matrices, model);
                            matrices.translate(0, -.35, .5);
                        }
                    }

                    model.setupAnim(renderer.createRenderState(shape, light));


                    // render
                    if (arm != null) {
                        arm.xRot = 0.0F;
                        arm.render(matrices, vertexConsumers.getBuffer(RenderType.entityTranslucent(texture)), light, OverlayTexture.NO_OVERLAY);
                    }

                    ci.cancel();
                }
            }
        }
    }
}
