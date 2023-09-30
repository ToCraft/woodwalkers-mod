package tocraft.walkers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerHostility;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.registry.WalkersEntityTags;

@Mixin(PiglinAi.class)
public class PiglinBrainMixin {

	@Inject(method = "isNearestValidAttackTarget", at = @At("RETURN"), cancellable = true)
	private static void shouldAttackShape(Piglin piglin, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
		boolean shouldAttack = cir.getReturnValue();

		if (shouldAttack && target instanceof Player player) {
			LivingEntity shape = PlayerShape.getCurrentShape(player);
			boolean hasHostility = PlayerHostility.hasHostility(player);

			if (shape != null) {
				// Piglins should not attack Piglins or Piglin Brutes, unless they have
				// hostility
				if (shape.getType().is(WalkersEntityTags.PIGLIN_FRIENDLY)) {
					cir.setReturnValue(false);
				}

				// Player has an shape but is not a piglin, check config for what to do
				else {
					if (Walkers.CONFIG.hostilesIgnoreHostileShapedPlayer() && shape instanceof Enemy) {

						// Check hostility for aggro on non-piglin hostiles
						if (!hasHostility) {
							cir.setReturnValue(false);
						} else {
							cir.setReturnValue(true);
						}
					}
				}
			}
		}
	}
}
