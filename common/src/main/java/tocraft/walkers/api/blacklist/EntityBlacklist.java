package tocraft.walkers.api.blacklist;

import net.minecraft.world.entity.EntityType;
import tocraft.walkers.Walkers;

import java.util.ArrayList;
import java.util.List;

public class EntityBlacklist {
    private static final List<EntityType<?>> blacklist = new ArrayList<>();

    public static boolean isBlacklisted(EntityType<?> entityType) {
        if (blacklist.contains(entityType)) {
            Walkers.LOGGER.warn("" + entityType);
        }
        return blacklist.contains(entityType);
    }

    public static void registerByType(EntityType<?> entityType) {
        blacklist.add(entityType);
    }
}
