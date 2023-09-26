package tocraft.walkers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Guardian;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.WalkersTickHandlers;
import tocraft.walkers.api.platform.VersionChecker;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;
import tocraft.walkers.network.NetworkHandler;
import tocraft.walkers.network.ServerNetworking;
import tocraft.walkers.registry.WalkersCommands;
import tocraft.walkers.registry.WalkersEntityTags;
import tocraft.walkers.registry.WalkersEventHandlers;

public class Walkers {

	public static final Logger LOGGER = LoggerFactory.getLogger(Walkers.class);
	public static final String MODID = "walkers";
	private static String VERSION = "";
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
		PlayerEvent.PLAYER_JOIN.register(player -> {
			// Send config sync packet
			FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
			packet.writeBoolean(WalkersConfig.getInstance().showPlayerNametag());
			packet.writeFloat(WalkersConfig.getInstance().unlockTimer());
			packet.writeBoolean(WalkersConfig.getInstance().unlockOveridesCurrentShape());
			packet.writeUtf(WalkersConfig.getInstance().shapeBlacklist().toString());
			NetworkManager.sendToPlayer(player, NetworkHandler.CONFIG_SYNC, packet);

			// Sync unlocked Walkers
			PlayerShapeChanger.sync(player);

			// check for updates
			VersionChecker.checkForUpdates(player);

			Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) ((ServerLevel) player.level())
					.getChunkSource().chunkMap).getEntityMap();
			trackers.forEach((entityid, tracking) -> {
				if (((ServerLevel) player.level()).getEntity(entityid) instanceof ServerPlayer)
					PlayerShape.sync(((ServerPlayer) ((ServerLevel) player.level()).getEntity(entityid)), player);
			});
		});
	}

	public static ResourceLocation id(String name) {
		return new ResourceLocation(MODID, name);
	}

	public static boolean hasFlyingPermissions(ServerPlayer player) {
		LivingEntity shape = PlayerShape.getCurrentShape(player);

		if (shape != null && WalkersConfig.getInstance().enableFlight()
				&& (shape.getType().is(WalkersEntityTags.FLYING) || shape instanceof FlyingMob)) {
			List<String> requiredAdvancements = WalkersConfig.getInstance().advancementsRequiredForFlight();

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
		return entity instanceof WaterAnimal || entity instanceof Guardian;
	}

	public static int getCooldown(EntityType<?> type) {
		String id = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
		return WalkersConfig.getInstance().getAbilityCooldownMap().getOrDefault(id, 20);
	}

	public static void setVersion(String version) {
		VERSION = version;
	}

	public static String getVersion() {
		return VERSION;
	}
}
