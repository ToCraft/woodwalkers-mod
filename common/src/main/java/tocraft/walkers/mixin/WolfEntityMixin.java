package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.registry.WalkersEntityTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin extends TameableEntity {

    private WolfEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }
    private static final TrackedData<Boolean> isDev =
        DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Inject(
            method = "initGoals",
            at = @At("RETURN")
    )
    private void addPlayerTarget(CallbackInfo ci) {
        this.targetSelector.add(7, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, false, false, player -> {
            // ensure wolves can attack players with an walkers similar to their normal prey
            if(!WalkersConfig.getInstance().wolvesAttack2ndShapedPrey()) {
                return false;
            }

            LivingEntity walkers = PlayerShape.getCurrentShape((PlayerEntity) player);

            // wolves should ignore players that look like their prey if they have an owner,
            // unless the config option is turned to true
            LivingEntity owner = this.getOwner();
            if(owner != null || WalkersConfig.getInstance().ownedwolvesAttack2ndShapedPrey()) {
                return false;
            }

            return walkers != null && walkers.getType().isIn(WalkersEntityTags.WOLF_PREY);
        }));
    }

    @Inject(
        method = "initDataTracker",
        at = @At("RETURN")
    )
    protected void onInitDataTracker(CallbackInfo ci) {
        ((WolfEntity)(Object)this).getDataTracker().startTracking(isDev, false);
    }

    @Inject(
        method = "writeCustomDataToNbt",
        at = @At("RETURN")
    )
    protected void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("isDev", ((WolfEntity)(Object)this).getDataTracker().get(isDev));
    }

    @Inject(
        method = "readCustomDataFromNbt",
        at = @At("RETURN")
    )
    protected void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        ((WolfEntity)(Object)this).getDataTracker().set(isDev, nbt.getBoolean("isDev"));
    }
}
