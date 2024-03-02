package tocraft.walkers.mixin;

import com.mojang.util.UUIDTypeAdapter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.impl.DimensionsRefresher;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.registry.WalkersEntityTags;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
@Mixin(Entity.class)
public abstract class EntityMixin implements DimensionsRefresher {

    @Shadow
    private EntityDimensions dimensions;

    @Shadow
    public abstract Pose getPose();

    @Shadow
    public abstract EntityDimensions getDimensions(Pose pose);

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract void setBoundingBox(AABB boundingBox);

    @Shadow
    protected boolean firstTick;

    @Shadow
    public abstract void move(MoverType type, Vec3 movement);

    @Shadow
    private float eyeHeight;

    @Shadow
    protected abstract float getEyeHeight(Pose pose, EntityDimensions dimensions);

    @Shadow
    @Nullable
    private Entity vehicle;

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
        Pose entityPose = this.getPose();
        EntityDimensions newDimensions = this.getDimensions(entityPose);

        this.dimensions = newDimensions;
        this.eyeHeight = this.getEyeHeight(entityPose, newDimensions);

        AABB box = this.getBoundingBox();
        this.setBoundingBox(new AABB(box.minX, box.minY, box.minZ, box.minX + newDimensions.width, box.minY + newDimensions.height, box.minZ + newDimensions.width));

        if (!this.firstTick) {
            float f = currentDimensions.width - newDimensions.width;
            this.move(MoverType.SELF, new Vec3(f, 0.0D, f));
        }
    }

    @Inject(at = @At("HEAD"), method = "getEyeHeight()F", cancellable = true)
    public void getEyeHeight(CallbackInfoReturnable<Float> cir) {
        if ((Entity) (Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
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
        if ((Object) this instanceof Player player && PlayerShape.getCurrentShape(player) != null && PlayerShape.getCurrentShape(player).getType().is(WalkersEntityTags.FALL_THROUGH_BLOCKS))
            player.noPhysics = true;
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "getVehicle", at = @At("RETURN"), cancellable = true)
    private void getClientVehicle(CallbackInfoReturnable<Entity> cir) {
        if ((Object) this instanceof AbstractClientPlayer clientPlayer && cir.getReturnValue() == null) {
            Optional<UUID> vehiclePlayerID = ((PlayerDataProvider) clientPlayer).walkers$getVehiclePlayerUUID();
            if (vehiclePlayerID.isPresent() && Objects.equals(UUIDTypeAdapter.fromString(Minecraft.getInstance().getUser().getUuid()), (vehiclePlayerID.get()))) {
                cir.setReturnValue(Minecraft.getInstance().player);
            }
        }
    }
}
