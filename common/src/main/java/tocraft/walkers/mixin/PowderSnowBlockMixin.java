package tocraft.walkers.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.WalkOnPowderSnow;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {
    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("RETURN"), cancellable = true)
    private static void handleWalkOnPowderSnowTrait(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && entity instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (TraitRegistry.has(shape, WalkOnPowderSnow.ID)) {
                cir.setReturnValue(true);
            }
        }
    }
}
