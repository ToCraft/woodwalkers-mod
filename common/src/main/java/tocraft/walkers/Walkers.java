package tocraft.walkers;

import dev.tocraft.craftedcore.config.ConfigLoader;
import dev.tocraft.craftedcore.event.common.EntityEvents;
import dev.tocraft.craftedcore.event.common.PlayerEvents;
import dev.tocraft.craftedcore.platform.VersionChecker;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.FlyingAnimal;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.WalkersTickHandlers;
import tocraft.walkers.api.data.DataManager;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.command.WalkersCommand;
import tocraft.walkers.eventhandler.LivingBreatheHandler;
import tocraft.walkers.eventhandler.RespawnHandler;
import tocraft.walkers.eventhandler.WalkersEventHandlers;
import tocraft.walkers.integrations.Integrations;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;
import tocraft.walkers.network.ServerNetworking;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.FlyingTrait;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings({"resource"})
public class Walkers {
    public static ProblemReporter PROBLEM_REPORTER = new ProblemReporter() {
        @Contract(pure = true)
        @Override
        public @NotNull ProblemReporter forChild(PathElement pathElement) {
            return this;
        }

        @Override
        public void report(@NotNull Problem problem) {
            Walkers.LOGGER.error(problem.description());
        }
    };

    @ApiStatus.Internal
    public static final Logger LOGGER = LoggerFactory.getLogger(Walkers.class);
    public static final String MODID = "walkers";
    public static final WalkersConfig CONFIG = ConfigLoader.register(MODID);
    @ApiStatus.Internal
    public static final List<UUID> devs = new ArrayList<>() {
        {
            add(UUID.fromString("1f63e38e-4059-4a4f-b7c4-0fac4a48e744"));
            add(UUID.fromString("494e1c8a-f733-43ed-8c1b-a2943fdc05f3"));
            add(UUID.fromString("eb83f5a3-397a-4e14-80bc-914ff91890f0"));
            add(UUID.fromString("17eae0aa-e445-489a-92ae-697d0a9d5731"));
            add(UUID.fromString("53430a02-dbd1-4b5c-9984-4119e38fec79"));
            add(UUID.fromString("50ee913f-a7d6-45ea-8d09-45fb3726aec1"));
            add(UUID.fromString("74b6d9b3-c8c1-40db-ab82-ccc290d1aa03"));
            add(UUID.fromString("d4a50582-c44e-4a0d-ab0c-9711e2cf4b29"));
            add(UUID.fromString("ccddb138-0b29-493f-9d27-0f51ed3a0578"));

        }
    };

    public void initialize() {
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
        VersionChecker.registerModrinthChecker(MODID, "woodwalkers", Component.literal("Woodwalkers"));

        PlayerEvents.PLAYER_JOIN.register(player -> {
            Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) player.level()
                    .getChunkSource().chunkMap).getEntityMap();
            trackers.forEach((entityid, tracking) -> {
                if (player.level().getEntity(entityid) instanceof ServerPlayer) {
                    PlayerShape.sync((ServerPlayer) player.level().getEntity(entityid), player);
                }
            });
        });
    }

    public static @NotNull ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    public static boolean hasFlyingPermissions(@NotNull ServerPlayer player) {
        if (player.isCreative()) {
            return true;
        }

        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (shape != null && Walkers.CONFIG.enableFlight
                && (TraitRegistry.has(shape, FlyingTrait.ID) || shape instanceof FlyingAnimal)) {
            List<String> requiredAdvancements = Walkers.CONFIG.advancementsRequiredForFlight;

            // requires at least 1 advancement, check if player has them
            if (!requiredAdvancements.isEmpty()) {

                boolean hasPermission = true;
                for (String requiredAdvancement : requiredAdvancements) {
                    AdvancementHolder advancement = Optional.ofNullable(player.getServer()).flatMap(s -> Optional.of(s.getAdvancements())).map(a -> a.get(ResourceLocation.parse(requiredAdvancement))).orElse(null);
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
        return devs.contains(uuid);
    }
}
