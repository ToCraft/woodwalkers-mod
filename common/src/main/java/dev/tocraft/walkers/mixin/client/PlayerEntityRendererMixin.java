package dev.tocraft.walkers.mixin.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.tocraft.craftedcore.util.Maths;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.api.model.ArmRenderingManipulator;
import dev.tocraft.walkers.api.model.EntityArms;
import dev.tocraft.walkers.api.model.EntityUpdater;
import dev.tocraft.walkers.api.model.EntityUpdaters;
import dev.tocraft.walkers.impl.ShapeRenderStateProvider;
import dev.tocraft.walkers.mixin.accessor.EntityAccessor;
import dev.tocraft.walkers.mixin.accessor.LivingEntityAccessor;
import dev.tocraft.walkers.mixin.client.accessor.LimbAnimatorAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@SuppressWarnings({"rawtypes", "unchecked"})
@Environment(EnvType.CLIENT)
@Mixin(value = AvatarRenderer.class, priority = 1001)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, AvatarRenderState, PlayerModel> {
    private PlayerEntityRendererMixin(EntityRendererProvider.Context ctx, PlayerModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("RETURN"))
    private void onCreateState(Avatar avatar, AvatarRenderState state, float f, CallbackInfo ci) {
        if (!(avatar instanceof AbstractClientPlayer player)) return;

        ((ShapeRenderStateProvider) state).walkers$setInvisRide(Minecraft.getInstance().options.getCameraType().isFirstPerson() && player.getVehicle() == Minecraft.getInstance().getCameraEntity());

        ((ShapeRenderStateProvider) state).walkers$setShape(() -> {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() || player.getVehicle() != Minecraft.getInstance().getCameraEntity()) {
                if (shape != null) {
                    walkers$updateShapeAttributes(player, shape);
                }
                return shape;
            }
            return null;
        });
    }

    @Unique
    private void walkers$updateShapeAttributes(@NotNull AvatarRenderState player, @NotNull EntityRenderState shape) {
        shape.y = player.y;
        shape.x = player.x;
        shape.z = player.z;
        shape.ageInTicks = player.ageInTicks;
        shape.passengerOffset = player.passengerOffset;
        shape.leashStates = player.leashStates;
        shape.lightCoords = player.lightCoords;

        if (shape instanceof LivingEntityRenderState livingState) {
            livingState.pose = player.pose;
            livingState.walkAnimationPos = player.walkAnimationPos;
            livingState.walkAnimationSpeed = player.walkAnimationSpeed;
            livingState.bodyRot = player.bodyRot;
            livingState.xRot = player.xRot;
            livingState.yRot = player.yRot;
            livingState.wornHeadAnimationPos = player.wornHeadAnimationPos;
            livingState.isInWater = player.isInWater;
            livingState.scale = player.scale;
            livingState.isFullyFrozen = player.isFullyFrozen;
            if (livingState instanceof ArmedEntityRenderState armedState) {
                armedState.mainArm = player.mainArm;
            }

            if (shape instanceof HumanoidRenderState humanoidShape) {
                humanoidShape.swimAmount = player.swimAmount;
                humanoidShape.isVisuallySwimming = player.isVisuallySwimming;
                humanoidShape.attackArm = player.attackArm;
                humanoidShape.attackTime = player.attackTime;
                humanoidShape.speedValue = player.speedValue;
                humanoidShape.isCrouching = player.isCrouching;
                humanoidShape.ticksUsingItem = player.ticksUsingItem;
                humanoidShape.isUsingItem = player.isUsingItem;
            }

            // fix wither heads
            else if (shape instanceof WitherRenderState witherState) {
                java.util.Arrays.fill(witherState.xHeadRots, player.xRot);
                java.util.Arrays.fill(witherState.yHeadRots, player.bodyRot + player.yRot);
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

        // fixes GeckoLib animation, "manually" calculates the delta movement
        float x = (float) ((player.getX() - player.xOld) / 1.62F);
        float y = (float) ((player.getY() - player.yOld) / 1.62F);
        float z = (float) ((player.getZ() - player.zOld) / 1.62F);
        Vec3 deltaMov = new Vec3(x, y, z);

        if (player != Minecraft.getInstance().getCameraEntity()) {
            shape.setDeltaMovement(deltaMov);
        } else {
            shape.setDeltaMovement(player.getDeltaMovement());
        }

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
        if (player != Minecraft.getInstance().player) {
            if (walkers$showName(player)) {
                shape.setCustomName(player.getCustomName());
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Unique
    private static boolean walkers$showName(@NotNull AbstractClientPlayer player) {
        Team team = player.getTeam();
        boolean showName = Walkers.CONFIG.showPlayerNametag;

        if (showName && team != null) {
            Team.Visibility visibility = team.getNameTagVisibility();
            Team localTeam = Minecraft.getInstance().player.getTeam();
            boolean sameTeam = Objects.equals(localTeam != null ? localTeam.getName() : null, team.getName());

            if (visibility == Team.Visibility.NEVER ||
                    (sameTeam && visibility == Team.Visibility.HIDE_FOR_OWN_TEAM) ||
                    (!sameTeam && visibility == Team.Visibility.HIDE_FOR_OTHER_TEAMS)) {

                showName = false;
            }
        }
        return showName;
    }

    @Override
    public void submit(AvatarRenderState state, PoseStack matrixStack, SubmitNodeCollector buffer, CameraRenderState camera) {
        if (((ShapeRenderStateProvider) state).walkers$getInvisRide()) {
            //FIXME: this feels illegal, prob. forgot shadows
            return;
        }

        LivingEntity shape = ((ShapeRenderStateProvider) state).walkers$getShape();

        // sync player data to shape
        if (shape != null && !state.isSpectator) {
            if (!state.isInvisibleToPlayer && !state.isInvisible) {
                EntityRenderer<LivingEntity, EntityRenderState> shapeRenderer = (EntityRenderer<LivingEntity, EntityRenderState>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(shape);

                EntityRenderState shapeState = shapeRenderer.createRenderState(shape, 1.0f);
                walkers$updateShapeAttributes(state, shapeState);

                shapeRenderer.submit(shapeState, matrixStack, buffer, camera);

            }

            return;
        }
        super.submit(state, matrixStack, buffer, camera);

    }

    @Inject(method = "getRenderOffset(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
    private void modifyPositionOffset(AvatarRenderState state, CallbackInfoReturnable<Vec3> cir) {
        LivingEntity shape = ((ShapeRenderStateProvider) state).walkers$getShape();
        if (shape != null) {
            if (shape instanceof TamableAnimal) {
                cir.setReturnValue(super.getRenderOffset(state));
            }
        }
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void onRenderArm(PoseStack matrices, SubmitNodeCollector vertexConsumers, int light, Identifier resourceLocation, ModelPart arm, boolean bl, CallbackInfo ci) {
        if (Minecraft.getInstance().getCameraEntity() instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            // sync player data to shape
            if (shape != null) {
                EntityRenderer<LivingEntity, ?> renderer = (EntityRenderer<LivingEntity, ?>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(shape);

                if (renderer instanceof LivingEntityRenderer livingRenderer) {
                    LivingEntityRenderState shapeState = ((LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>) livingRenderer).createRenderState(shape, 1.0f);
                    shapeState.lightCoords = light;
                    Identifier texture = livingRenderer.getTextureLocation(shapeState);
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

                    model.setupAnim(renderer.createRenderState(shape, 1.0f));

                    // render
                    if (arm != null) {
                        arm.xRot = 0.0F;
                        vertexConsumers.submitModelPart(arm, matrices, RenderTypes.entityTranslucent(texture), light, OverlayTexture.NO_OVERLAY, null);
                    }

                    ci.cancel();
                }
            }
        }
    }
}
