package tocraft.walkers.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;

@Mixin(Rabbit.RabbitAvoidEntityGoal.class)
public class RabbitAvoidEntityGoalMixin<T extends LivingEntity> extends AvoidEntityGoal<T>  {
    public RabbitAvoidEntityGoalMixin(PathfinderMob mob, Class<T> entityClassToAvoid, float maxDistance, double walkSpeedModifier, double sprintSpeedModifier) {
        super(mob, entityClassToAvoid, maxDistance, walkSpeedModifier, sprintSpeedModifier);
    }

    @Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
    private void onCanUse(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (this.toAvoid instanceof Player player && PlayerShape.getCurrentShape(player) instanceof Rabbit) {
                cir.setReturnValue(false);
            }
        }
    }
}
