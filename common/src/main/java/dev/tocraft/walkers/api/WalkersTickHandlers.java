package dev.tocraft.walkers.api;

import dev.tocraft.walkers.impl.tick.shapes.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class WalkersTickHandlers {

    private static final Map<EntityType<?>, WalkersTickHandler<?>> HANDLERS = new LinkedHashMap<>();

    @ApiStatus.Internal
    public static void initialize() {
        register(EntityTypes.WARDEN, new WardenTickHandler());
        register(EntityTypes.FROG, new FrogTickHandler());
        register(EntityTypes.CAMEL, new JumpBoostTickHandler<>(0));
        register(EntityTypes.SNOW_GOLEM, new SnowGolemTickHandler());
        register(EntityTypes.RABBIT, new JumpBoostTickHandler<>(1));
        register(EntityTypes.GOAT, new JumpBoostTickHandler<>(0)); // raise default goat jump high
        register(EntityTypes.MAGMA_CUBE, new JumpBoostTickHandler<>(2));
    }

    public static <T extends LivingEntity> void register(EntityType<@NotNull T> type, WalkersTickHandler<T> handler) {
        HANDLERS.put(type, handler);
    }

    @ApiStatus.Internal
    public static Map<EntityType<?>, WalkersTickHandler<?>> getHandlers() {
        return HANDLERS;
    }
}
