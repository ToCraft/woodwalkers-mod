package tocraft.walkers.api.event;

import org.jetbrains.annotations.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import tocraft.craftedcore.events.Event;
import tocraft.craftedcore.events.EventBuilder;

public interface WalkersSwapCallback {
    Event<WalkersSwapCallback> EVENT = EventBuilder.createEventResult(WalkersSwapCallback.class);

    Event.Result swap(ServerPlayer player, @Nullable LivingEntity to);
}
