package dev.tocraft.walkers.api.data;

import dev.tocraft.craftedcore.registration.SynchronizedReloadListenerRegistry;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.api.data.abilities.AbilityDataManager;
import dev.tocraft.walkers.api.data.blacklist.EntityBlacklistDataManager;
import dev.tocraft.walkers.api.data.traits.TraitDataManager;
import dev.tocraft.walkers.api.data.variants.TypeProviderDataManager;

public class DataManager {
    public static void initialize() {
        SynchronizedReloadListenerRegistry.register(new AbilityDataManager(), Walkers.id("ability_data_manager"));
        SynchronizedReloadListenerRegistry.register(new TypeProviderDataManager(), Walkers.id("variants_data_manager"));
        SynchronizedReloadListenerRegistry.register(new TraitDataManager(), Walkers.id("trait_data_manager"));
        SynchronizedReloadListenerRegistry.register(new EntityBlacklistDataManager(), Walkers.id("entity_blacklist_data_manager"));
    }
}
