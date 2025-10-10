package mcp.mobius.waila.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IEntityAccessor {
    /**
     * @return level where the 'looking' takes place
     */
    Level getWorld();

    /**
     * @return player looking
     */
    Player getPlayer();

    /**
     *
     * @return entity being looked at
     */
    <T extends Entity> T getEntity();
}
