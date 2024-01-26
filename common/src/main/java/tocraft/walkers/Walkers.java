package tocraft.walkers;

import dev.architectury.event.events.common.PlayerEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tocraft.craftedcore.VIPs;
import tocraft.craftedcore.config.ConfigLoader;
import tocraft.craftedcore.platform.VersionChecker;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.WalkersTickHandlers;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.command.WalkersCommand;
import tocraft.walkers.integrations.Integrations;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;
import tocraft.walkers.network.ServerNetworking;
import tocraft.walkers.registry.WalkersEntityTags;
import tocraft.walkers.registry.WalkersEventHandlers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Walkers {

    public static final Logger LOGGER = LoggerFactory.getLogger(Walkers.class);
    public static final String MODID = "walkers";
    public static final String MAVEN_URL = "https://maven.tocraft.dev/public/dev/tocraft/walkers/maven-metadata.xml";
    public static final WalkersConfig CONFIG = ConfigLoader.read(MODID, WalkersConfig.class);
    public static List<UUID> devs = new ArrayList<>();

    static {
        devs.add(UUID.fromString("1f63e38e-4059-4a4f-b7c4-0fac4a48e744"));
        devs.add(UUID.fromString("494e1c8a-f733-43ed-8c1b-a2943fdc05f3"));
    }

    public void initialize() {
        AbilityRegistry.init();
        WalkersEventHandlers.initialize();
        WalkersCommand.register();
        ServerNetworking.initialize();
        ServerNetworking.registerUseAbilityPacketHandler();
        registerJoinSyncPacket();
        WalkersTickHandlers.initialize();

        // handle integrations
        Integrations.initialize();
    }

    public static void registerJoinSyncPacket() {
        try {
            VersionChecker.registerMavenChecker(MODID, new URL(MAVEN_URL), Component.translatable("key.categories.walkers"));
        } catch (MalformedURLException ignored) {
        }

        PlayerEvent.PLAYER_JOIN.register(player -> {
            Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) ((ServerLevel) player.level())
                    .getChunkSource().chunkMap).getEntityMap();
            trackers.forEach((entityid, tracking) -> {
                if (player.level().getEntity(entityid) instanceof ServerPlayer)
                    PlayerShape.sync(((ServerPlayer) player.serverLevel().getEntity(entityid)), player);
            });
        });
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MODID, name);
    }

    public static boolean hasFlyingPermissions(ServerPlayer player) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (shape != null && Walkers.CONFIG.enableFlight
                && (shape.getType().is(WalkersEntityTags.FLYING) || shape instanceof FlyingMob)) {
            List<String> requiredAdvancements = Walkers.CONFIG.advancementsRequiredForFlight;

            // requires at least 1 advancement, check if player has them
            if (!requiredAdvancements.isEmpty()) {

                boolean hasPermission = true;
                for (String requiredAdvancement : requiredAdvancements) {
                    Advancement advancement = player.server.getAdvancements()
							.getAdvancement(new ResourceLocation(requiredAdvancement));
                    AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);

                    if (!progress.isDone()) {
                        hasPermission = false;
                    }
                }

                return hasPermission;
            }

            return true;
        }

        return false;
    }

    public static boolean isAquatic(LivingEntity entity) {
        return entity != null && entity.getMobType().equals(MobType.WATER);
    }

    public static int getCooldown(EntityType<?> type) {
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
        return Walkers.CONFIG.abilityCooldownMap.getOrDefault(id, 20);
    }

    public static boolean hasSpecialShape(UUID uuid) {
        return devs.contains(uuid) || VIPs.getPatreons().contains(uuid);
    }
}
