package tocraft.walkers.api.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.api.variant.ShapeType;

public interface ShapeEvents {
    Event<UnlockShapeCallback> UNLOCK_SHAPE = EventFactory.createEventResult(UnlockShapeCallback.class);

    Event<ShapeSwapCallback> SWAP_SHAPE = EventFactory.createEventResult(ShapeSwapCallback.class);

    interface ShapeSwapCallback {
        EventResult swap(ServerPlayer player, @Nullable LivingEntity to);
    }

    interface UnlockShapeCallback {
        EventResult unlock(ServerPlayer player, ShapeType<?> type);
    }
}
