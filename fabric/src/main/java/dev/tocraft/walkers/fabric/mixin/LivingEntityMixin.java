package dev.tocraft.walkers.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.traits.ShapeTrait;
import dev.tocraft.walkers.traits.TraitRegistry;
import dev.tocraft.walkers.traits.impl.AquaticTrait;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("ConstantValue")
@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyExpressionValue(method = "travelInWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z"))
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
