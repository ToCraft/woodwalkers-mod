package tocraft.walkers.api.blacklist;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import tocraft.walkers.Walkers;
import tocraft.walkers.integrations.Integrations;

import java.util.ArrayList;
import java.util.List;

public class EntityBlacklist {
    private static final List<EntityType<?>> typeBlacklist = new ArrayList<>();
    private static final List<TagKey<EntityType<?>>> tagBlacklist = new ArrayList<>();

    public static void registerDefault() {
        // support deprecated entity tags
        registerByTag(TagKey.create(Walkers.getEntityTypeRegistry().key(), Walkers.id("blacklisted")));

        // handle Integrations
        Integrations.registerEntityBlacklist();
    }

    public static boolean isBlacklisted(EntityType<?> entityType) {
        if (typeBlacklist.contains(entityType)) return true;
        for (TagKey<EntityType<?>> entityTypeTagKey : tagBlacklist) {
            if (entityType.is(entityTypeTagKey)) return true;
        }

        return Walkers.CONFIG.entityBlacklistIsWhitelist != Walkers.CONFIG.entityBlacklist.contains(EntityType.getKey(entityType).toString());
    }

    public static void registerByType(EntityType<?> entityType) {
        if (!typeBlacklist.contains(entityType)) typeBlacklist.add(entityType);
    }

    public static void registerByTag(TagKey<EntityType<?>> entityTypeTag) {
        if (!tagBlacklist.contains(entityTypeTag)) tagBlacklist.add(entityTypeTag);
    }

    public static void clearAll() {
        typeBlacklist.clear();
        tagBlacklist.clear();
    }
}
