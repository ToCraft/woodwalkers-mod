package tocraft.walkers.impl;

import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Pass player data to the shape
 */
@ApiStatus.Internal
public interface ShapeDataProvider {

    boolean walkers$isShape();

    void walkers$setIsShape(boolean isShape);

    @Nullable
    DamageSource walkers$playerDamageSource();

    void walkers$setPlayerDamageSource(DamageSource playerDamageSource);
}
