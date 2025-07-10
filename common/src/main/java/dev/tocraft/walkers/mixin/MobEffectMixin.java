package dev.tocraft.walkers.mixin;

import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.traits.ShapeTrait;
import dev.tocraft.walkers.traits.TraitRegistry;
import dev.tocraft.walkers.traits.impl.ImmunityTrait;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEffect.class)
public class MobEffectMixin {
    @SuppressWarnings({"RedundantCast", "EqualsBetweenInconvertibleTypes"})
    @Inject(method = "applyInstantenousEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyEffectTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;I)Z"), cancellable = true)
    private void onApplyEffect(ServerLevel serverLevel, Entity entity, Entity entity2, LivingEntity livingEntity, int i, double d, CallbackInfo ci) {
        if (livingEntity instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            for (ShapeTrait<LivingEntity> immunityTrait : TraitRegistry.get(shape, ImmunityTrait.ID)) {
                if (((ImmunityTrait<LivingEntity>) immunityTrait).effect.equals((MobEffect) (Object) this)) {
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
