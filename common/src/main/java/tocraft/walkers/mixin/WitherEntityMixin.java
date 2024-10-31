package tocraft.walkers.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerShape;

import java.util.List;

@Mixin(WitherBoss.class)
public abstract class WitherEntityMixin extends Monster {

    private WitherEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private void removeInvalidPlayerTargets(CallbackInfo ci, @Local @NotNull List<LivingEntity> list) {
        list.removeIf(entity -> {
            if (entity instanceof Player player) {
                LivingEntity shape = PlayerShape.getCurrentShape(player);

                // potentially ignore undead walkers players
                if (shape != null && shape.isInvertedHealAndHarm()) {
                    if (this.getTarget() != null) {
                        // if this wither's target is not equal to the current entity
                        return !this.getTarget().getUUID().equals(entity.getUUID());
                    } else {
                        return true;
                    }
                }
            }
            return false;
        });

    }
}
