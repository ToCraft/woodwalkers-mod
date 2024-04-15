package tocraft.walkers.api.blacklist;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class EntityBlacklist {
    private static final List<EntityType<?>> typeBlacklist = new ArrayList<>();
    private static final List<TagKey<EntityType<?>>> tagBlacklist = new ArrayList<>();

    public static boolean isBlacklisted(EntityType<?> entityType) {
        if (typeBlacklist.contains(entityType)) return true;
        for (TagKey<EntityType<?>> entityTypeTagKey : tagBlacklist) {
            if (entityType.is(entityTypeTagKey)) return true;
        }
        return false;
    }

    public static void registerByType(EntityType<?> entityType) {
        typeBlacklist.add(entityType);
    }

    public static void registerByTag(TagKey<EntityType<?>> entityTypeTag) {
        tagBlacklist.add(entityTypeTag);
    }
}
