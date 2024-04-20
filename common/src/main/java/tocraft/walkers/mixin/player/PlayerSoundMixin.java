package tocraft.walkers.mixin.player;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.mixin.LivingEntityMixin;
import tocraft.walkers.mixin.accessor.EntityAccessor;
import tocraft.walkers.mixin.accessor.LivingEntityAccessor;
import tocraft.walkers.mixin.accessor.MobEntityAccessor;

@Mixin(Player.class)
public abstract class PlayerSoundMixin extends LivingEntityMixin {

    protected PlayerSoundMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void getHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (Walkers.CONFIG.useShapeSounds && shape != null) {
            cir.setReturnValue(((LivingEntityAccessor) shape).callGetHurtSound(source));
        }
    }

    @Unique
    private int shape_ambientSoundChance = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickAmbientSounds(CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (!level().isClientSide && Walkers.CONFIG.playAmbientSounds && shape instanceof Mob mobShape) {

            if (this.isAlive() && this.random.nextInt(1000) < this.shape_ambientSoundChance++) {
                // reset sound delay
                this.shape_ambientSoundChance = -mobShape.getAmbientSoundInterval();

                // play ambient sound
                SoundEvent sound = ((MobEntityAccessor) mobShape).callGetAmbientSound();
                if (sound != null) {
                    float volume = ((LivingEntityAccessor) mobShape).callGetSoundVolume();
                    float pitch = ((LivingEntityAccessor) mobShape).callGetVoicePitch();

                    // By default, players can not hear their own ambient noises.
                    // This is because ambient noises can be very annoying.
                    if (Walkers.CONFIG.hearSelfAmbient) {
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), sound,
                                this.getSoundSource(), volume, pitch);
                    } else {
                        this.level().playSound((Player) (Object) this, this.getX(), this.getY(), this.getZ(), sound,
                                this.getSoundSource(), volume, pitch);
                    }
                }
            }
        }
    }


    @Inject(method = "playStepSound", at = @At("HEAD"))
    private void handleSpeSounds(BlockPos pos, BlockState state, CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (shape != null) {
            ((EntityAccessor) shape).shape_callPlayStepSound(pos, state);
        }
    }

    @Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    private void getDeathSound(CallbackInfoReturnable<SoundEvent> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (Walkers.CONFIG.useShapeSounds && shape != null) {
            cir.setReturnValue(((LivingEntityAccessor) shape).callGetDeathSound());
        }
    }

    @Inject(method = "getFallSounds", at = @At("HEAD"), cancellable = true)
    private void getFallSounds(CallbackInfoReturnable<LivingEntity.Fallsounds> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (Walkers.CONFIG.useShapeSounds && shape != null) {
            cir.setReturnValue(shape.getFallSounds());
        }
    }
}
