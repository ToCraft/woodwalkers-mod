package tocraft.walkers.mixin;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.impl.DimensionsRefresher;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.HumanoidTrait;
import tocraft.walkers.traits.impl.NoPhysicsTrait;

@SuppressWarnings("ConstantConditions")
@Mixin(Entity.class)
public abstract class EntityMixin implements DimensionsRefresher {

    @Shadow
    private EntityDimensions dimensions;

    @Shadow
    protected boolean firstTick;

    @Shadow
    private float eyeHeight;


    @Inject(method = "getBbWidth", at = @At("HEAD"), cancellable = true)
    private void getBbWidth(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                cir.setReturnValue(shape.getBbWidth());
            }
        }
    }

    @Inject(method = "getBbHeight", at = @At("HEAD"), cancellable = true)
    private void getBbHeight(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                cir.setReturnValue(shape.getBbHeight());
            }
        }
    }

    @Override
    public void shape_refreshDimensions() {
        EntityDimensions currentDimensions = this.dimensions;
        Pose entityPose = ((Entity) (Object) this).getPose();
        EntityDimensions newDimensions = ((Entity) (Object) this).getDimensions(entityPose);

        this.dimensions = newDimensions;
        this.eyeHeight = ((Entity) (Object) this).getEyeHeight(entityPose);

        AABB box = ((Entity) (Object) this).getBoundingBox();
        ((Entity) (Object) this).setBoundingBox(new AABB(box.minX, box.minY, box.minZ, box.minX + newDimensions.width, box.minY + newDimensions.height, box.minZ + newDimensions.width));

        if (!this.firstTick) {
            float f = currentDimensions.width - newDimensions.width;
            ((Entity) (Object) this).move(MoverType.SELF, new Vec3(f, 0.0D, f));
        }
    }

    @Inject(at = @At("HEAD"), method = "getEyeHeight()F", cancellable = true)
    public void getEyeHeight(CallbackInfoReturnable<Float> cir) {
        if ((Entity) (Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                if (((Entity) (Object) this).isCrouching() && TraitRegistry.has(shape, HumanoidTrait.ID)) {
                    cir.setReturnValue(shape.getEyeHeight(Pose.CROUCHING) * 1.27F / 1.62F);
                    return;
                }
                cir.setReturnValue(shape.getEyeHeight());
            }
        }
    }

    @Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
    private void fireImmune(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                cir.setReturnValue(shape.getType().fireImmune());
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void goThroughBlocks(CallbackInfo ci) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape != null) {
                if (TraitRegistry.has(shape, NoPhysicsTrait.ID)) {
                    player.noPhysics = true;
                }
            }
        }
    }
}
