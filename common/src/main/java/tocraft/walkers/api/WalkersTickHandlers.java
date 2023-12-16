package tocraft.walkers.api;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.impl.tick.shapes.JumpBoostTickHandler;
import tocraft.walkers.impl.tick.shapes.SnowGolemTickHandler;

public class WalkersTickHandlers {

    private static final Map<EntityType<?>, WalkersTickHandler<?>> HANDLERS = new HashMap<>();

    public static void initialize() {
        register(EntityType.SNOW_GOLEM, new SnowGolemTickHandler());
        register(EntityType.RABBIT, new JumpBoostTickHandler<>(1));
    }

    public static <T extends LivingEntity> void register(EntityType<T> type, WalkersTickHandler<T> handler) {
        HANDLERS.put(type, handler);
    }

    public static Map<EntityType<?>, WalkersTickHandler<?>> getHandlers() {
        return HANDLERS;
    }
}
