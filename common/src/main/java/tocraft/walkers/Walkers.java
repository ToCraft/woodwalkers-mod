package tocraft.walkers;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Guardian;
import tocraft.craftedcore.config.ConfigLoader;
import tocraft.craftedcore.events.common.PlayerEvents;
import tocraft.craftedcore.platform.Platform;
import tocraft.craftedcore.platform.VersionChecker;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.WalkersTickHandlers;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;
import tocraft.walkers.network.ServerNetworking;
import tocraft.walkers.registry.WalkersCommands;
import tocraft.walkers.registry.WalkersEntityTags;
import tocraft.walkers.registry.WalkersEventHandlers;

public class Walkers {

	public static final Logger LOGGER = LoggerFactory.getLogger(Walkers.class);
	public static final String MODID = "walkers";
	public static String versionURL = "https://raw.githubusercontent.com/ToCraft/woodwalkers-mod/arch-1.20.1/gradle.properties";
	public static final WalkersConfig CONFIG = ConfigLoader.read(MODID, WalkersConfig.class);
	public static List<String> devs = new ArrayList<>();

	static {
		devs.add("1f63e38e-4059-4a4f-b7c4-0fac4a48e744");
	}

	public void initialize() {
		AbilityRegistry.init();
		WalkersEventHandlers.initialize();
		WalkersCommands.init();
		ServerNetworking.initialize();
		ServerNetworking.registerUseAbilityPacketHandler();
		registerJoinSyncPacket();
		WalkersTickHandlers.initialize();
	}

	public static void registerJoinSyncPacket() {
		PlayerEvents.PLAYER_JOIN.register(player -> {
			// Sync unlocked Walkers
			PlayerShapeChanger.sync(player);

			// check for updates
			@Nullable
			String newVersion = VersionChecker.checkForNewVersion(versionURL);
			if (newVersion != null && !Platform.getMod(MODID).getVersion().contains(newVersion))
				player.sendSystemMessage(Component.translatable("walkers.update", newVersion));

			Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) ((ServerLevel) player.level())
					.getChunkSource().chunkMap).getEntityMap();
			trackers.forEach((entityid, tracking) -> {
				if (((ServerLevel) player.level()).getEntity(entityid) instanceof ServerPlayer)
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
					AdvancementHolder advancement = player.server.getAdvancements().get(new ResourceLocation(requiredAdvancement));
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
		return entity instanceof WaterAnimal || entity instanceof Guardian;
	}

	public static int getCooldown(EntityType<?> type) {
		String id = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
		return Walkers.CONFIG.abilityCooldownMap.getOrDefault(id, 20);
	}
}
