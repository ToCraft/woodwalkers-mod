package tocraft.walkers.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerShape;

@Mixin(Creeper.class)
public abstract class CreeperEntityMixin extends Monster {

    private CreeperEntityMixin(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
            method = "registerGoals",
            at = @At("RETURN")
    )
    private void addCustomGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(
                this,
                Player.class,
                entity -> {
                    if (entity instanceof Player player) {
                        LivingEntity shape = PlayerShape.getCurrentShape(player);
                        return shape instanceof Ocelot || shape instanceof Cat;
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
