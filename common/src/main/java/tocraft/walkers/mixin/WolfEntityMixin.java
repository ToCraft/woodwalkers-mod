package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.registry.WalkersEntityTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Wolf.class)
public abstract class WolfEntityMixin extends TamableAnimal {

    private WolfEntityMixin(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }
    private static final EntityDataAccessor<Boolean> isDev =
        SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);

    @Inject(
            method = "registerGoals",
            at = @At("RETURN")
    )
    private void addPlayerTarget(CallbackInfo ci) {
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false, player -> {
            // ensure wolves can attack players with a shape similar to their normal prey
            if(!WalkersConfig.getInstance().wolvesAttack2ndShapedPrey()) {
                return false;
            }

            LivingEntity shape = PlayerShape.getCurrentShape((Player) player);

            // wolves should ignore players that look like their prey if they have an owner,
            // unless the config option is turned to true
            LivingEntity owner = this.getOwner();
            if(owner != null || WalkersConfig.getInstance().ownedwolvesAttack2ndShapedPrey()) {
                return false;
            }

            return shape != null && shape.getType().is(WalkersEntityTags.WOLF_PREY);
        }));
    }

    @Inject(
        method = "defineSynchedData",
        at = @At("RETURN")
    )
    protected void onInitDataTracker(CallbackInfo ci) {
        ((Wolf)(Object)this).getEntityData().define(isDev, false);
    }

    @Inject(
        method = "addAdditionalSaveData",
        at = @At("RETURN")
    )
    protected void onWriteCustomDataToNbt(CompoundTag nbt, CallbackInfo ci) {
        nbt.putBoolean("isDev", ((Wolf)(Object)this).getEntityData().get(isDev));
    }

    @Inject(
        method = "readAdditionalSaveData",
        at = @At("RETURN")
    )
    protected void onReadCustomDataFromNbt(CompoundTag nbt, CallbackInfo ci) {
        ((Wolf)(Object)this).getEntityData().set(isDev, nbt.getBoolean("isDev"));
    }
}
