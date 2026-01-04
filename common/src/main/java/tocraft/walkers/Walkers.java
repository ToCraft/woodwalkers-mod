package tocraft.walkers;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
//#if MC>1201
import net.minecraft.advancements.AdvancementHolder;
//#else
//$$ import net.minecraft.advancements.Advancement;
//#endif
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tocraft.craftedcore.VIPs;
import tocraft.craftedcore.config.ConfigLoader;
import tocraft.craftedcore.event.common.EntityEvents;
import tocraft.craftedcore.event.common.PlayerEvents;
import tocraft.craftedcore.patched.CEntity;
import tocraft.craftedcore.patched.CRegistries;
import tocraft.craftedcore.patched.Identifier;
import tocraft.craftedcore.patched.TComponent;
import tocraft.craftedcore.platform.VersionChecker;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.WalkersTickHandlers;
import tocraft.walkers.api.data.DataManager;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.command.WalkersCommand;
import tocraft.walkers.eventhandler.LivingBreatheHandler;
import tocraft.walkers.eventhandler.RespawnHandler;
import tocraft.walkers.integrations.Integrations;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;
import tocraft.walkers.network.ServerNetworking;
import tocraft.walkers.eventhandler.WalkersEventHandlers;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.FlyingTrait;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"resource"})
public class Walkers {

    public static final Logger LOGGER = LoggerFactory.getLogger(Walkers.class);
    public static final String MODID = "walkers";
    public static final WalkersConfig CONFIG = ConfigLoader.register(MODID);
    public static final List<UUID> devs = new ArrayList<>();

    static {
        devs.add(UUID.fromString("1f63e38e-4059-4a4f-b7c4-0fac4a48e744"));
        devs.add(UUID.fromString("494e1c8a-f733-43ed-8c1b-a2943fdc05f3"));
        devs.add(UUID.fromString("eb83f5a3-397a-4e14-80bc-914ff91890f0"));
        devs.add(UUID.fromString("17eae0aa-e445-489a-92ae-697d0a9d5731"));
        devs.add(UUID.fromString("53430a02-dbd1-4b5c-9984-4119e38fec79"));
        devs.add(UUID.fromString("50ee913f-a7d6-45ea-8d09-45fb3726aec1"));
        devs.add(UUID.fromString("c75a7f4b-daaa-4ad0-a82f-05ed5542025c"));
    }

    public void initialize() {
        MixinExtrasBootstrap.init();

        Integrations.initIntegrations();

        TraitRegistry.initialize();
        AbilityRegistry.initialize();

        WalkersCommand.initialize();
        WalkersEventHandlers.initialize();
        ServerNetworking.initialize();
        registerJoinSyncPacket();
        WalkersTickHandlers.initialize();
        DataManager.initialize();
        Integrations.initialize();

        PlayerEvents.PLAYER_RESPAWN.register(new RespawnHandler());
        EntityEvents.LIVING_BREATHE.register(new LivingBreatheHandler());
    }

    public static void registerJoinSyncPacket() {
        VersionChecker.registerModrinthChecker(MODID, "woodwalkers", TComponent.literal("Woodwalkers"));

        PlayerEvents.PLAYER_JOIN.register(player -> {
            Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) ((ServerLevel) CEntity.level(player))
                    .getChunkSource().chunkMap).getEntityMap();
            trackers.forEach((entityid, tracking) -> {
                if (CEntity.level(player).getEntity(entityid) instanceof ServerPlayer) {
                    PlayerShape.sync((ServerPlayer) CEntity.level(player).getEntity(entityid), player);
                }
            });
        });
    }

    public static ResourceLocation id(String name) {
        return Identifier.parse(MODID, name);
    }

    public static boolean hasFlyingPermissions(ServerPlayer player) {
        if (player.isCreative()) {
            return true;
        }

        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (shape != null && Walkers.CONFIG.enableFlight
                && (TraitRegistry.has(shape, FlyingTrait.ID) || shape instanceof FlyingMob)) {
            List<String> requiredAdvancements = Walkers.CONFIG.advancementsRequiredForFlight;

            // requires at least 1 advancement, check if player has them
            if (!requiredAdvancements.isEmpty()) {

                boolean hasPermission = true;
                for (String requiredAdvancement : requiredAdvancements) {
                    //#if MC>1201
                    AdvancementHolder advancement = player.server.getAdvancements().get(Identifier.parse(requiredAdvancement));
                    //#else
                    //$$ Advancement advancement = player.server.getAdvancements().getAdvancement(Identifier.parse(requiredAdvancement));
                    //#endif
                    if (advancement != null) {
                        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);

                        if (!progress.isDone()) {
                            hasPermission = false;
                        }
                    }
                }

                return hasPermission;
            }

            return true;
        }

        return false;
    }

    public static boolean isPlayerBlacklisted(UUID uuid) {
        return CONFIG.playerBlacklistIsWhitelist != CONFIG.playerUUIDBlacklist.contains(uuid);
    }

    public static boolean hasSpecialShape(UUID uuid) {
        return devs.contains(uuid) || VIPs.getCachedPatreons().contains(uuid);
    }

    public static Registry<EntityType<?>> getEntityTypeRegistry() {
        //noinspection unchecked
        return ((Registry<EntityType<?>>) CRegistries.getRegistry(Identifier.parse("entity_type")));
    }

    public static <R> DataResult<R> dataError(final String message) {
        //#if MC>1182
        return DataResult.error(() -> message);
        //#else
        //$$ return DataResult.error(message);
        //#endif
    }
}
