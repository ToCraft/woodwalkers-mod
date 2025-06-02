package tocraft.walkers.neoforge.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.traits.ShapeTrait;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.AquaticTrait;

@SuppressWarnings("ConstantValue")
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyExpressionValue(method = "travelInFluid(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/material/FluidState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z"))
    public boolean applyWaterCreatureSwimSpeedBoost(boolean org) {
        if (!org && (Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            // Apply 'Dolphin's Grace' status effect benefits if the player's shape is a
            // water creature
            for (ShapeTrait<LivingEntity> trait : TraitRegistry.get(shape, AquaticTrait.ID)) {
                if (((AquaticTrait<LivingEntity>) trait).isAquatic) {
                    return true;
                }
            }
        }

        return org;
    }
}
