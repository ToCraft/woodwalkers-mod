package tocraft.walkers.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tocraft.craftedcore.network.NetworkManager;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.PlayerAbilities;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.network.impl.SpecialSwapPackets;
import tocraft.walkers.network.impl.SwapPackets;
import tocraft.walkers.network.impl.UnlockPackets;

public class ServerNetworking implements NetworkHandler {

	public static void initialize() {
		SwapPackets.registerWalkersRequestPacketHandler();
		UnlockPackets.registerShapeUnlockRequestPacketHandler();
		SpecialSwapPackets.registerDevRequestPacketHandler();
	}

	public static void registerUseAbilityPacketHandler() {
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, USE_ABILITY, (buf, context) -> {
			Player player = context.getPlayer();

			context.getPlayer().getServer().execute(() -> {
				LivingEntity shape = PlayerShape.getCurrentShape(player);

				// Verify we should use ability for the player's current shape
				if (shape != null) {
					EntityType<?> shapeType = shape.getType();

					if (AbilityRegistry.has(shapeType)) {

						// Check cooldown
						if (PlayerAbilities.canUseAbility(player)) {
							AbilityRegistry.get(shapeType).onUse(player, shape, context.getPlayer().level());
							PlayerAbilities.setCooldown(player, AbilityRegistry.get(shapeType).getCooldown(shape));
							PlayerAbilities.sync((ServerPlayer) player);
						}
					}
				}
			});
		});
	}
}
