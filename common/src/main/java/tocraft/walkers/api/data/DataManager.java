package tocraft.walkers.api.data;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
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
        ReloadListenerRegistry.register(PackType.SERVER_DATA, ABILITY_DATA_MANAGER, Walkers.id("ability_data_manager"));
        ReloadListenerRegistry.register(PackType.SERVER_DATA, TYPE_PROVIDER_DATA_MANAGER, Walkers.id("variants_data_manager"));
        ReloadListenerRegistry.register(PackType.SERVER_DATA, SKILL_DATA_MANAGER, Walkers.id("skill_data_manager"));
        ReloadListenerRegistry.register(PackType.SERVER_DATA, ENTITY_BLACKLIST_DATA_MANAGER, Walkers.id("entity_blacklist_data_manager"));

        // sync packets on player join
        PlayerEvent.PLAYER_JOIN.register(DataManager::sendAllToPlayer);
    }

    public static void sendAllToPlayer(ServerPlayer player) {
        ABILITY_DATA_MANAGER.sendSyncPacket(player);
        TYPE_PROVIDER_DATA_MANAGER.sendSyncPacket(player);
        SKILL_DATA_MANAGER.sendSyncPacket(player);
        ENTITY_BLACKLIST_DATA_MANAGER.sendSyncPacket(player);
    }

    @Environment(EnvType.CLIENT)
    public static void registerReceiver() {
        ABILITY_DATA_MANAGER.registerPacketReceiver();
        TYPE_PROVIDER_DATA_MANAGER.registerPacketReceiver();
        SKILL_DATA_MANAGER.registerPacketReceiver();
        ENTITY_BLACKLIST_DATA_MANAGER.registerPacketReceiver();
    }
}
