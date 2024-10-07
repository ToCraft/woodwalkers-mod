package tocraft.walkers.fabric.mixin;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.injection.At;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.traits.ShapeTrait;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.*;

@Mixin(Player.class)
public class PlayerMixin {
    //#if MC>1194
    @ModifyExpressionValue(method = "getDestroySpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z"))
    //#else
    //$$ @ModifyExpressionValue(method = "getDestroySpeed", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;onGround:Z", opcode = Opcodes.GETFIELD))
    //#endif
    private boolean onModifyBreakingSpeedOnFlight(boolean original) {
        if (TraitRegistry.has(PlayerShape.getCurrentShape((Player) (Object) this), FlyingTrait.ID)) {
            return true;
        }
        else if (((Player) (Object) this).isEyeInFluid(FluidTags.WATER)) {
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
