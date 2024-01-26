package tocraft.walkers.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;

@Mixin(AvoidEntityGoal.class)
public class AvoidEntityGoalMixin<T extends LivingEntity> {
    @Shadow
    protected T toAvoid;
    @Final
    @Shadow
    protected PathfinderMob mob;

    @Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
    private void onCanUse(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && this.toAvoid instanceof Player player) {
            // prevent cats and ocelots to run from cats and ocelots
            if (this.mob instanceof Cat || this.mob instanceof Ocelot) {
                if (PlayerShape.getCurrentShape(player) instanceof Ocelot || PlayerShape.getCurrentShape(player) instanceof Cat) {
                    cir.setReturnValue(false);
                }
            }
            // prevent rabbits to run from rabbits
            else if (this.mob instanceof Rabbit) {
                if (PlayerShape.getCurrentShape(player) instanceof Rabbit) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
