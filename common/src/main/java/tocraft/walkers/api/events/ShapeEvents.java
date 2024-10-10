package tocraft.walkers.api.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.event.Event;
import tocraft.craftedcore.event.EventFactory;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.api.variant.ShapeType;

public final class ShapeEvents {
    /**
     * Called when using the default unlocking mechanic
     */
    public static final Event<UnlockShapeCallback> UNLOCK_SHAPE = EventFactory.createWithInteractionResult();

    /**
     * Called when morphing
     */
    public static final Event<ShapeSwapCallback> SWAP_SHAPE = EventFactory.createWithInteractionResult();

    /**
     * Called when a {@link ShapeAbility ShapeAbility} is used
     */
    public static final Event<UseShapeAbilityCallback> USE_SHAPE_ABILITY = EventFactory.createWithInteractionResult();

    @FunctionalInterface
    public interface ShapeSwapCallback {
        InteractionResult swap(ServerPlayer player, @Nullable LivingEntity to);
    }

    @FunctionalInterface
    public interface UnlockShapeCallback {
        InteractionResult unlock(ServerPlayer player, ShapeType<?> type);
    }

    @FunctionalInterface
    public interface UseShapeAbilityCallback {
        InteractionResult use(Player player, ShapeAbility<?> ability);
    }
}
