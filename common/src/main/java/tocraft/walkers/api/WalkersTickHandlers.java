package tocraft.walkers.api;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.impl.tick.shapes.FrogTickHandler;
import tocraft.walkers.impl.tick.shapes.JumpBoostTickHandler;
import tocraft.walkers.impl.tick.shapes.SnowGolemTickHandler;
import tocraft.walkers.impl.tick.shapes.WardenTickHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class WalkersTickHandlers {

    private static final Map<EntityType<?>, WalkersTickHandler<?>> HANDLERS = new LinkedHashMap<>();

    public static void initialize() {
        register(EntityType.WARDEN, new WardenTickHandler());
        register(EntityType.FROG, new FrogTickHandler());
        register(EntityType.SNOW_GOLEM, new SnowGolemTickHandler());
        register(EntityType.RABBIT, new JumpBoostTickHandler<>(1));
        register(EntityType.GOAT, new JumpBoostTickHandler<>(2));
        register(EntityType.CAMEL, new JumpBoostTickHandler<>(0));
        register(EntityType.MAGMA_CUBE, new JumpBoostTickHandler<>(2));
        register(EntityType.SLIME, new JumpBoostTickHandler<>(2));
    }

    public static <T extends LivingEntity> void register(EntityType<T> type, WalkersTickHandler<T> handler) {
        HANDLERS.put(type, handler);
    }

    public static Map<EntityType<?>, WalkersTickHandler<?>> getHandlers() {
        return HANDLERS;
    }
}
