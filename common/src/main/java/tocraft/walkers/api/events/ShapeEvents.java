package tocraft.walkers.api.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.event.Event;
import tocraft.craftedcore.event.EventFactory;
import tocraft.walkers.api.variant.ShapeType;

public final class ShapeEvents {
    public static final Event<UnlockShapeCallback> UNLOCK_SHAPE = EventFactory.createWithInteractionResult();

    public static final Event<ShapeSwapCallback> SWAP_SHAPE = EventFactory.createWithInteractionResult();

    public interface ShapeSwapCallback {
        InteractionResult swap(ServerPlayer player, @Nullable LivingEntity to);
    }

    public interface UnlockShapeCallback {
        InteractionResult unlock(ServerPlayer player, ShapeType<?> type);
    }
}
