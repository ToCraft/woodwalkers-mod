package tocraft.walkers;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import tocraft.craftedcore.events.client.ClientPlayerEvents;
import tocraft.craftedcore.events.client.ClientTickEvents;
import tocraft.craftedcore.registration.client.KeyMappingRegistry;
import tocraft.walkers.ability.AbilityOverlayRenderer;
import tocraft.walkers.api.ApplicablePacket;
import tocraft.walkers.api.model.EntityArms;
import tocraft.walkers.api.model.EntityUpdaters;
import tocraft.walkers.impl.join.ClientPlayerJoinHandler;
import tocraft.walkers.impl.tick.KeyPressHandler;
import tocraft.walkers.network.ClientNetworking;

public class WalkersClient {
	public static final KeyMapping UNLOCK_KEY = new KeyMapping("key.walkers_unlock", InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_U, "key.categories.walkers");
	public static final KeyMapping TRANSFORM_KEY = new KeyMapping("key.walkers", InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_G, "key.categories.walkers");
	public static final KeyMapping ABILITY_KEY = new KeyMapping("key.walkers_ability", InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_R, "key.categories.walkers");

	private static final Set<ApplicablePacket> SYNC_PACKET_QUEUE = new HashSet<>();

	public void initialize() {
		KeyMappingRegistry.register(ABILITY_KEY);
		KeyMappingRegistry.register(TRANSFORM_KEY);
		KeyMappingRegistry.register(UNLOCK_KEY);

		// Register client-side event handlers
		EntityUpdaters.init();
		AbilityOverlayRenderer.register();
		EntityArms.init();

		// Register event handlers
		ClientTickEvents.CLIENT_PRE.register(new KeyPressHandler());
		ClientNetworking.registerPacketHandlers();
		ClientPlayerEvents.CLIENT_PLAYER_JOIN.register(new ClientPlayerJoinHandler());
	}

	// We do this because the Architectury "player log in" network event runs before
	// MinecraftClient#player exists.
	public static Set<ApplicablePacket> getSyncPacketQueue() {
		return SYNC_PACKET_QUEUE;
	}
}
