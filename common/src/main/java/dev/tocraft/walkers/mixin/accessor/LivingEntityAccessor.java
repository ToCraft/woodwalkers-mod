package dev.tocraft.walkers.mixin.accessor;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor
    boolean isJumping();

    @Invoker
    void callUpdatingUsingItem();

    @Invoker
    SoundEvent callGetHurtSound(DamageSource source);

    @Invoker
    SoundEvent callGetDeathSound();

    @Invoker
    void callPlayBlockFallSound();

    @Invoker
    int callCalculateFallDamage(double fallDistance, float damageMultiplier);

    @Invoker
    float callGetSoundVolume();

    @Invoker
    float callGetVoicePitch();

    @Invoker
    void callSetLivingEntityFlag(int mask, boolean value);

    @Accessor
    float getSwimAmount();

    @Accessor
    void setSwimAmount(float swimAmount);

    @Accessor
    float getSwimAmountO();

    @Accessor
    void setSwimAmountO(float swimAmount);

    @Invoker
    float callGetJumpPower(float multiplier);
}
