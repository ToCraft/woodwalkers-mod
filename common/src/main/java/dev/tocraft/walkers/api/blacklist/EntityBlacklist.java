package dev.tocraft.walkers.api.blacklist;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.integrations.AbstractIntegration;
import dev.tocraft.walkers.integrations.Integrations;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public class EntityBlacklist {
    private static final List<EntityType<?>> typeBlacklist = new ArrayList<>();
    private static final List<TagKey<EntityType<?>>> tagBlacklist = new ArrayList<>();

    @ApiStatus.Internal
    public static void registerDefault() {
        // support deprecated entity tags
        registerByTag(TagKey.create(Registries.ENTITY_TYPE, Walkers.id("blacklisted")));

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

    /**
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerEntityBlacklist()} Integration.registerEntityBlacklist()}}
     */
    public static void registerByType(EntityType<?> entityType) {
        if (!typeBlacklist.contains(entityType)) typeBlacklist.add(entityType);
    }

    /**
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerEntityBlacklist()} Integration.registerEntityBlacklist()}}
     */
    public static void registerByTag(TagKey<EntityType<?>> entityTypeTag) {
        if (!tagBlacklist.contains(entityTypeTag)) tagBlacklist.add(entityTypeTag);
    }

    @ApiStatus.Internal
    public static void clearAll() {
        typeBlacklist.clear();
        tagBlacklist.clear();
    }
}
