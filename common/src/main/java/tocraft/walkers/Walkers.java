package tocraft.walkers;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import dev.architectury.event.events.common.PlayerEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import tocraft.walkers.api.data.DataManager;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.command.WalkersCommand;
import tocraft.walkers.integrations.Integrations;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;
import tocraft.walkers.network.ServerNetworking;
import tocraft.walkers.registry.WalkersEventHandlers;
import tocraft.walkers.skills.ShapeSkill;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.AquaticSkill;
import tocraft.walkers.skills.impl.FlyingSkill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Walkers {

    public static final Logger LOGGER = LoggerFactory.getLogger(Walkers.class);
    public static final String MODID = "walkers";
    public static final WalkersConfig CONFIG = ConfigLoader.read(MODID, WalkersConfig.class);
    public static final List<UUID> devs = new ArrayList<>();

    static {
        devs.add(UUID.fromString("1f63e38e-4059-4a4f-b7c4-0fac4a48e744"));
        devs.add(UUID.fromString("494e1c8a-f733-43ed-8c1b-a2943fdc05f3"));
    }

    public void initialize() {
        MixinExtrasBootstrap.init();

        AbilityRegistry.init();
        SkillRegistry.init();
        WalkersEventHandlers.initialize();
        WalkersCommand.register();
        ServerNetworking.initialize();
        registerJoinSyncPacket();
        WalkersTickHandlers.initialize();
        DataManager.initialize();
        Integrations.initialize();
    }

    public static void registerJoinSyncPacket() {
        VersionChecker.registerDefaultGitHubChecker(MODID, "ToCraft", "woodwalkers-mod", Component.literal("Woodwalkers"));

        PlayerEvent.PLAYER_JOIN.register(player -> {
            Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) ((ServerLevel) player.level())
                    .getChunkSource().chunkMap).getEntityMap();
            trackers.forEach((entityid, tracking) -> {
                if (player.level().getEntity(entityid) instanceof ServerPlayer) {
                    PlayerShape.sync(((ServerPlayer) player.serverLevel().getEntity(entityid)), player);
                }
            });
        });
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MODID, name);
    }

    public static boolean hasFlyingPermissions(ServerPlayer player) {
        if (player.isCreative())
            return true;

        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (shape != null && Walkers.CONFIG.enableFlight
                && (SkillRegistry.has(shape, FlyingSkill.ID) || shape instanceof FlyingMob)) {
            List<String> requiredAdvancements = Walkers.CONFIG.advancementsRequiredForFlight;

            // requires at least 1 advancement, check if player has them
            if (!requiredAdvancements.isEmpty()) {

                boolean hasPermission = true;
                for (String requiredAdvancement : requiredAdvancements) {
                    AdvancementHolder advancement = player.server.getAdvancements().get(new ResourceLocation(requiredAdvancement));
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

    /**
     * @param entity the shape to be checked
     * @return 0 - water mob, 1 - land and water mob, 2 - land mob
     */
    public static int isAquatic(LivingEntity entity) {
        if (entity != null) {
            for (ShapeSkill<LivingEntity> aquaticSkill : SkillRegistry.get(entity, AquaticSkill.ID)) {
                return ((AquaticSkill<LivingEntity>) aquaticSkill).isAquatic;
            }
            return entity.getMobType().equals(MobType.WATER) ? 0 : 2;
        } else {
            return 2;
        }
    }

    public static boolean isPlayerBlacklisted(UUID uuid) {
        return CONFIG.playerBlacklistIsWhitelist != CONFIG.playerUUIDBlacklist.contains(uuid);
    }

    public static boolean hasSpecialShape(UUID uuid) {
        return devs.contains(uuid) || VIPs.getCachedPatreons().contains(uuid);
    }
}
