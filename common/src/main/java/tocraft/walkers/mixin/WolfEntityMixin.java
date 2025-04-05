package tocraft.walkers.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.Walkers;

import net.minecraft.world.entity.animal.wolf.Wolf;

@SuppressWarnings({"RedundantCast"})
@Mixin(Wolf.class)
public abstract class WolfEntityMixin extends TamableAnimal {
    @Unique
    private static final ResourceLocation walkers$SPECIAL_WILD = Walkers.id("textures/entity/wolf/special_wild.png");
    @Unique
    private static final ResourceLocation walkers$SPECIAL_TAMED = Walkers.id("textures/entity/wolf/special_tame.png");
    @Unique
    private static final ResourceLocation walkers$SPECIAL_ANGRY = Walkers.id("textures/entity/wolf/special_angry.png");


    private WolfEntityMixin(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }

    @SuppressWarnings("all")
    @Unique
    private static final EntityDataAccessor<Boolean> walkers$isSpecial = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        if (this.hasCustomName()) {
            if (this.getCustomName().getString().equalsIgnoreCase("Patreon")) {
                ((Entity) (Object) this).getEntityData().set(walkers$isSpecial, true);
            } else {
                // reset texture on rename
                ((Entity) (Object) this).getEntityData().set(walkers$isSpecial, false);
            }
        }
    }

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    protected void onInitDataTracker(SynchedEntityData.@NotNull Builder builder, CallbackInfo ci) {
        builder.define(walkers$isSpecial, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void onWriteCustomDataToNbt(@NotNull CompoundTag nbt, CallbackInfo ci) {
        if (((Entity) (Object) this).getEntityData().get(walkers$isSpecial)) {
            nbt.putBoolean("isSpecial", true);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void onReadCustomDataFromNbt(@NotNull CompoundTag nbt, CallbackInfo ci) {
        ((Entity) (Object) this).getEntityData().set(walkers$isSpecial, nbt.getBooleanOr("isSpecial", false));
    }

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void setSpecialTexture(CallbackInfoReturnable<ResourceLocation> cir) {
        CompoundTag nbt = new CompoundTag();
        this.saveWithoutId(nbt);

        if (nbt.getBooleanOr("isSpecial", false)) {
            if (this.isTame()) {
                cir.setReturnValue(walkers$SPECIAL_TAMED);
            } else {
                cir.setReturnValue(((Wolf) (Object) this).isAngry() ? walkers$SPECIAL_ANGRY : walkers$SPECIAL_WILD);
            }
        }
    }
}
