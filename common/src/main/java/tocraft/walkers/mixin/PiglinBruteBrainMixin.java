package tocraft.walkers.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBruteAi;
import net.minecraft.world.entity.player.Player;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerHostility;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.registry.WalkersEntityTags;

@Mixin(PiglinBruteAi.class)
public class PiglinBruteBrainMixin {
    
    @Inject( method = "getTargetIfWithinRange", at = @At("RETURN"), cancellable = true)
    private static void getTargetIfWithinRange(AbstractPiglin piglinBrute, MemoryModuleType<? extends LivingEntity> memoryType, CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
		cir.setReturnValue(piglinBrute.getBrain().getMemory(memoryType).filter((livingEntity) -> {
			if (livingEntity instanceof Player player) {
				LivingEntity shape = PlayerShape.getCurrentShape(player);

				if (shape != null) {
					// Piglins should not attack Piglins or Piglin Brutes, unless they have
					// hostility
					if (shape.getType().is(WalkersEntityTags.PIGLIN_FRIENDLY)) {
						return false;
					}

					// Player has a shape but is not a piglin, check config for what to do
					else {
						if (Walkers.CONFIG.hostilesIgnoreHostileShapedPlayer && shape instanceof Enemy) {

							// Check hostility for aggro on non-piglin hostiles
							if (PlayerHostility.hasHostility(player)) {
								return false;
							}
						}
					}
				}
			}
			return livingEntity.closerThan(piglinBrute, 12.0);
		}));
	}
}