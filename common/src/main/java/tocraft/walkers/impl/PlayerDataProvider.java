package tocraft.walkers.impl;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.api.variant.ShapeType;

import java.util.Optional;
import java.util.UUID;

@ApiStatus.Internal
public interface PlayerDataProvider {

    @Nullable
    ShapeType<?> walkers$get2ndShape();

    void walkers$set2ndShape(ShapeType<?> unlocked);

    int walkers$getRemainingHostilityTime();

    void walkers$setRemainingHostilityTime(int max);

    int walkers$getAbilityCooldown();

    void walkers$setAbilityCooldown(int cooldown);

    @Nullable
    LivingEntity walkers$getCurrentShape();

    void walkers$setCurrentShape(@Nullable LivingEntity shape);

    void walkers$updateShapes(@Nullable LivingEntity shape);
}
