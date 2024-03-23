package tocraft.walkers.impl;

import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.Nullable;

/**
 * Pass player data to the shape
 */
public interface ShapeDataProvider {

    boolean walkers$isShape();

    void walkers$setIsShape(boolean isShape);

    @Nullable DamageSource walkers$playerDamageSource();

    void walkers$setPlayerDamageSource(DamageSource playerDamageSource);
}
