package tocraft.walkers.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.PiglinSpecificSensor;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerShape;

@Mixin(PiglinSpecificSensor.class)
public class PiglinSensorMixin {
    @Inject(method = "doTick", at = @At("RETURN"))
    private void runFromZombifiedShapes(ServerLevel level, LivingEntity entity, CallbackInfo ci) {
        for (LivingEntity livingEntity : entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty()).findAll(livingEntity -> true)) {
            if (livingEntity instanceof Player player && PlayerShape.getCurrentShape(player) != null && PiglinAi.isZombified(PlayerShape.getCurrentShape(player).getType())) {
                entity.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, player);
            }
        }
    }
}
