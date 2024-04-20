package tocraft.walkers.impl;

import tocraft.walkers.mixin.player.PlayerEntityMixin;

/**
 * Duck interface for accessing information about nearby playing music in {@link PlayerEntityMixin}.
 */
public interface NearbySongAccessor {
    boolean shape_isNearbySongPlaying();
}
