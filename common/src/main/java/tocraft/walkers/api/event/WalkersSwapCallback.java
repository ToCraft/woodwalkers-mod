package tocraft.walkers.api.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface WalkersSwapCallback {
    Event<WalkersSwapCallback> EVENT = EventFactory.createEventResult(WalkersSwapCallback.class);

    EventResult swap(ServerPlayer player, @Nullable LivingEntity to);
}
