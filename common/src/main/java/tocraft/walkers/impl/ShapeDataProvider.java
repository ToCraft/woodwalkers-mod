package tocraft.walkers.impl;

import org.jetbrains.annotations.ApiStatus;

/**
 * Pass player data to the shape
 */
@ApiStatus.Internal
public interface ShapeDataProvider {

    int walkers$shapedPlayer();

    void walkers$ShapedPlayer(int id);
}
