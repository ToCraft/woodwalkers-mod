package tocraft.walkers.api.data;

import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.server.packs.PackType;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.data.abilities.AbilityDataManager;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;

public class DataManager {
    public static final AbilityDataManager ABILITY_DATA_MANAGER = new AbilityDataManager();
    public static final TypeProviderDataManager TYPE_PROVIDER_DATA_MANAGER = new TypeProviderDataManager();

    public static void initialize() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, ABILITY_DATA_MANAGER, Walkers.id("ability_data_manager"));
        ReloadListenerRegistry.register(PackType.SERVER_DATA, TYPE_PROVIDER_DATA_MANAGER, Walkers.id("variants_data_manager"));
    }
}
