package tocraft.walkers.api;

import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface ApplicablePacket {
    void apply(Player player);
}
