package tocraft.walkers.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({})
@Mixin(Wolf.class)
public abstract class WolfEntityMixin extends TamableAnimal {

    private WolfEntityMixin(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }

    @SuppressWarnings("all")
    @Unique
    private static final EntityDataAccessor<Boolean> walkers$isSpecial = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        if (this.hasCustomName() && this.getCustomName().getString().equalsIgnoreCase("Patreon"))
            ((Wolf) (Object) this).getEntityData().set(walkers$isSpecial, true);
        else
            ((Wolf) (Object) this).getEntityData().set(walkers$isSpecial, false);
    }

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    protected void onInitDataTracker(CallbackInfo ci) {
        ((Wolf) (Object) this).getEntityData().define(walkers$isSpecial, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void onWriteCustomDataToNbt(CompoundTag nbt, CallbackInfo ci) {
        nbt.putBoolean("isSpecial", ((Wolf) (Object) this).getEntityData().get(walkers$isSpecial));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void onReadCustomDataFromNbt(CompoundTag nbt, CallbackInfo ci) {
        ((Wolf) (Object) this).getEntityData().set(walkers$isSpecial, nbt.getBoolean("isSpecial"));
    }
}
