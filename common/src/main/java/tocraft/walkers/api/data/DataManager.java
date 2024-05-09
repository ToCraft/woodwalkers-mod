package tocraft.walkers.api.data;

import tocraft.craftedcore.network.ModernNetworking;
import tocraft.craftedcore.registration.SynchronizedReloadListenerRegistry;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.data.abilities.AbilityDataManager;
import tocraft.walkers.api.data.blacklist.EntityBlacklistDataManager;
import tocraft.walkers.api.data.skills.SkillDataManager;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;

public class DataManager {
    private static final AbilityDataManager ABILITY_DATA_MANAGER = new AbilityDataManager();
    private static final TypeProviderDataManager TYPE_PROVIDER_DATA_MANAGER = new TypeProviderDataManager();
    private static final SkillDataManager SKILL_DATA_MANAGER = new SkillDataManager();
    private static final EntityBlacklistDataManager ENTITY_BLACKLIST_DATA_MANAGER = new EntityBlacklistDataManager();

    public static void initialize() {
        SynchronizedReloadListenerRegistry.register(ABILITY_DATA_MANAGER, Walkers.id("ability_data_manager"));
        SynchronizedReloadListenerRegistry.register(TYPE_PROVIDER_DATA_MANAGER, Walkers.id("variants_data_manager"));
        SynchronizedReloadListenerRegistry.register(SKILL_DATA_MANAGER, Walkers.id("skill_data_manager"));
        SynchronizedReloadListenerRegistry.register(ENTITY_BLACKLIST_DATA_MANAGER, Walkers.id("entity_blacklist_data_manager"));

        ModernNetworking.registerType(ABILITY_DATA_MANAGER.RELOAD_SYNC);
        ModernNetworking.registerType(TYPE_PROVIDER_DATA_MANAGER.RELOAD_SYNC);
        ModernNetworking.registerType(SKILL_DATA_MANAGER.RELOAD_SYNC);
        ModernNetworking.registerType(ENTITY_BLACKLIST_DATA_MANAGER.RELOAD_SYNC);
    }
}
