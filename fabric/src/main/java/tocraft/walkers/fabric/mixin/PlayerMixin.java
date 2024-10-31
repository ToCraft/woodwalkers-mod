package tocraft.walkers.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.traits.ShapeTrait;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.AquaticTrait;
import tocraft.walkers.traits.impl.FlyingTrait;

@Mixin(Player.class)
public class PlayerMixin {
    @ModifyExpressionValue(method = "getDestroySpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"))
    private boolean onModifyBreakingSpeedOnFlight(boolean original) {
        if (TraitRegistry.has(PlayerShape.getCurrentShape((Player) (Object) this), FlyingTrait.ID)) {
            return true;
        } else if (((Player) (Object) this).isEyeInFluid(FluidTags.WATER)) {
            for (ShapeTrait<LivingEntity> aquaticTrait : TraitRegistry.get(PlayerShape.getCurrentShape((Player) (Object) this), AquaticTrait.ID)) {
                if (((AquaticTrait<LivingEntity>) aquaticTrait).isAquatic) {
                    return true;
                }
            }
        }
        return original;
    }

    @ModifyExpressionValue(method = "getDestroySpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean onModifyBreakingSpeedWhenSwimming(boolean original) {
        for (ShapeTrait<LivingEntity> aquaticTrait : TraitRegistry.get(PlayerShape.getCurrentShape((Player) (Object) this), AquaticTrait.ID)) {
            if (((AquaticTrait<LivingEntity>) aquaticTrait).isAquatic) {
                return false;
            }
        }
        return original;
    }
}
