package tocraft.walkers.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.traits.ShapeTrait;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.ImmunityTrait;

@Mixin(MobEffectInstance.class)
public class MobEffectInstanceMixin {
    //#if MC>1204
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyEffectTick(Lnet/minecraft/world/entity/LivingEntity;I)Z"))
    private boolean onApplyEffect(MobEffect effect, LivingEntity livingEntity, int amplifier, Operation<Boolean> original) {
        if (livingEntity instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            for (ShapeTrait<LivingEntity> immunityTrait : TraitRegistry.get(shape, ImmunityTrait.ID)) {
                if (((ImmunityTrait<LivingEntity>) immunityTrait).effect.equals(effect)) {
                    return true;
                }
            }
        }
        return original.call(effect, livingEntity, amplifier);
    }
    //#elseif MC>=1202
    //$$ @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyEffectTick(Lnet/minecraft/world/entity/LivingEntity;I)V"))
    //$$ private void onApplyEffect(MobEffect effect, LivingEntity livingEntity, int amplifier, Operation<Void> original) {
    //$$     if (livingEntity instanceof Player player) {
    //$$         LivingEntity shape = PlayerShape.getCurrentShape(player);
    //$$         for (ShapeTrait<LivingEntity> immunityTrait : TraitRegistry.get(shape, ImmunityTrait.ID)) {
    //$$             if (((ImmunityTrait<LivingEntity>) immunityTrait).effect.equals(effect)) {
    //$$                 return;
    //$$             }
    //$$         }
    //$$     }
    //$$     original.call(effect, livingEntity, amplifier);
    //$$ }
    //#else
    //$$ @WrapOperation(method = "applyEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyEffectTick(Lnet/minecraft/world/entity/LivingEntity;I)V"))
    //$$ private void onApplyEffect(MobEffect effect, LivingEntity livingEntity, int amplifier, Operation<Boolean> original) {
    //$$     if (livingEntity instanceof Player player) {
    //$$         LivingEntity shape = PlayerShape.getCurrentShape(player);
    //$$         for (ShapeTrait<LivingEntity> immunityTrait : TraitRegistry.get(shape, ImmunityTrait.ID)) {
    //$$             if (((ImmunityTrait<LivingEntity>) immunityTrait).effect.equals(effect)) {
    //$$                 return;
    //$$             }
    //$$         }
    //$$     }
    //$$     original.call(effect, livingEntity, amplifier);
    //$$ }
    //#endif
}
