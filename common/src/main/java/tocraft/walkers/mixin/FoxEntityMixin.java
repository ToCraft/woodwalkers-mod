package tocraft.walkers.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import tocraft.walkers.api.PlayerShape;

import java.util.function.Predicate;

@Mixin(Fox.class)
public abstract class FoxEntityMixin extends Animal {

    @Shadow
    @Final
    @Mutable
    private static Predicate<Entity> AVOID_PLAYERS;

    private FoxEntityMixin(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    // Change the default "flee from player," predicate to ignore players disguised
    // as Foxes.
    // Hopefully nobody else needs to modify fox fleeing behavior.
    static {
        AVOID_PLAYERS = entity -> {
            boolean isShapedPlayer = false;

            if (entity instanceof Player player) {
                LivingEntity shape = PlayerShape.getCurrentShape(player);
                if (shape instanceof Fox) {
                    isShapedPlayer = true;
                }
            }

            return !entity.isDiscrete() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) && !isShapedPlayer;
        };
    }
}
