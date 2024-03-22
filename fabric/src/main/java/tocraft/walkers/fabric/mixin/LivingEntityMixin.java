package tocraft.walkers.fabric.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;

@SuppressWarnings("ConstantConditions")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected abstract int increaseAirSupply(int air);

    protected LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setAirSupply(I)V", ordinal = 2))
    private void cancelAirIncrement(LivingEntity livingEntity, int air) {
        // Aquatic creatures should not regenerate breath on land
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                if (Walkers.isAquatic(shape) < 1) {
                    return;
                }
            }
        }

        this.setAirSupply(this.increaseAirSupply(this.getAirSupply()));
    }
}
