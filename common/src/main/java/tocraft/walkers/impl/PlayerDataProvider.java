package tocraft.walkers.impl;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.api.variant.ShapeType;

public interface PlayerDataProvider {

    ShapeType<?> walkers$get2ndShape();
    void walkers$set2ndShape(ShapeType<?> unlocked);

    int walkers$getRemainingHostilityTime();
    void walkers$setRemainingHostilityTime(int max);

    int walkers$getAbilityCooldown();
    void walkers$setAbilityCooldown(int cooldown);

    LivingEntity walkers$getCurrentShape();
    void walkers$setCurrentShape(@Nullable LivingEntity shape);
    boolean walkers$updateShapes(@Nullable LivingEntity shape);

    ShapeType<?> walkers$getCurrentShapeType();
}
