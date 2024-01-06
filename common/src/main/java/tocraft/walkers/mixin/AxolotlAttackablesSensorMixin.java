package tocraft.walkers.mixin;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.AxolotlAttackablesSensor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;

@Mixin(AxolotlAttackablesSensor.class)
public class AxolotlAttackablesSensorMixin {
    // Axolotl attack Player shaped as Axolotl Hunt Targets
    @Inject(method = "isHuntTarget", at = @At("RETURN"), cancellable = true)
    private void onIsHuntTarget(LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof Player player && !cir.getReturnValue()) {
            if (PlayerShape.getCurrentShape(player) != null)
                cir.setReturnValue(!attacker.getBrain().hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN) && PlayerShape.getCurrentShape(player).getType().is(EntityTypeTags.AXOLOTL_HUNT_TARGETS));
        }
    }
}
