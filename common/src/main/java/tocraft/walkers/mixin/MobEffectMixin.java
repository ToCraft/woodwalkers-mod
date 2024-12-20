package tocraft.walkers.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.traits.ShapeTrait;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.ImmunityTrait;

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
