package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public abstract class AbstractSkeletonEntityMixin extends HostileEntity {

    private AbstractSkeletonEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "initGoals",
            at = @At("RETURN")
    )
    private void addCustomGoals(CallbackInfo ci) {
        this.goalSelector.add(3, new FleeEntityGoal<>(
                this,
                PlayerEntity.class,
                entity -> {
                    if (entity instanceof PlayerEntity player) {
                        LivingEntity shape = PlayerShape.getCurrentShape(player);
                        return shape != null && shape.getType().equals(EntityType.WOLF);
                    }

                    return true;
                },
                6.0F,
                1.0D,
                1.2D,
                player -> true
        ));
    }
}
