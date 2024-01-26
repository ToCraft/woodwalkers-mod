package tocraft.walkers.api.model;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Implementers should use the provided {@link Player} instance to update the provided {@link Entity}'s properties.
 *
 * <p>{@link EntityUpdater} instances can be registered and retrieved through {@link EntityUpdaters}.
 *
 * @param <Entity>
 */
@FunctionalInterface
public interface EntityUpdater<Entity extends LivingEntity> {

    /**
     * Updates the given {@link Entity} using properties from the given {@link Player}.
     *
     * <p>Called once every render update on the client.
     *
     * @param from {@link Player} to copy properties from
     * @param to   {@link Entity} to copy properties to
     */
    void update(Player from, Entity to);
}