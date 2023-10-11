package tocraft.walkers.api;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.impl.tick.shapes.FrogTickHandler;
import tocraft.walkers.impl.tick.shapes.JumpBoostTickHandler;
import tocraft.walkers.impl.tick.shapes.WardenTickHandler;

public class WalkersTickHandlers {

    private static final Map<EntityType<?>, WalkersTickHandler<?>> HANDLERS = new HashMap<>();

    public static void initialize() {
        register(EntityType.WARDEN, new WardenTickHandler());
        register(EntityType.FROG, new FrogTickHandler());
        register(EntityType.RABBIT, new JumpBoostTickHandler<>(1));
        register(EntityType.GOAT, new JumpBoostTickHandler<>(2));
        register(EntityType.CAMEL, new JumpBoostTickHandler<>(0));
    }

    public static <T extends LivingEntity> void register(EntityType<T> type, WalkersTickHandler<T> handler) {
        HANDLERS.put(type, handler);
    }

    public static Map<EntityType<?>, WalkersTickHandler<?>> getHandlers() {
        return HANDLERS;
    }
}
