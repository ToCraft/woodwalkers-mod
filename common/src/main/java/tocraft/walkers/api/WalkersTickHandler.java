package tocraft.walkers.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface WalkersTickHandler<Z extends Entity> {

    void tick(Player player, Z entity);
}
