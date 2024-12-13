package tocraft.walkers.api.data;

import tocraft.craftedcore.registration.SynchronizedReloadListenerRegistry;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.data.abilities.AbilityDataManager;
import tocraft.walkers.api.data.blacklist.EntityBlacklistDataManager;
import tocraft.walkers.api.data.traits.TraitDataManager;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;

public class DataManager {
    public static void initialize() {
        SynchronizedReloadListenerRegistry.register(new AbilityDataManager(), Walkers.id("ability_data_manager"));
        SynchronizedReloadListenerRegistry.register(new TypeProviderDataManager(), Walkers.id("variants_data_manager"));
        SynchronizedReloadListenerRegistry.register(new TraitDataManager(), Walkers.id("trait_data_manager"));
        SynchronizedReloadListenerRegistry.register(new EntityBlacklistDataManager(), Walkers.id("entity_blacklist_data_manager"));
    }
}
