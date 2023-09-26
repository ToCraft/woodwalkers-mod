package tocraft.walkers.impl.tick;

import org.jetbrains.annotations.Nullable;

import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.platform.SyncedVars;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.impl.DevSwapPackets;
import tocraft.walkers.network.impl.SwapPackets;
import tocraft.walkers.network.impl.UnlockPackets;

public class KeyPressHandler implements ClientTickEvent.Client {
	private float currentTimer = 0f;

	@Override
	public void tick(Minecraft client) {
		assert client.player != null;

		if (WalkersClient.ABILITY_KEY.consumeClick())
			handleAbilityKey(client);

		if (WalkersClient.TRANSFORM_KEY.consumeClick()) {
			handleTransformKey(client);
		}

		if (WalkersClient.UNLOCK_KEY.isDown())
			handleUnlockKey(client);

		else if (currentTimer != SyncedVars.getUnlockTimer())
			currentTimer = SyncedVars.getUnlockTimer();
	}

	private void handleAbilityKey(Minecraft client) {
		// TODO: maybe the check should be on the server to allow for ability extension
		// mods?
		// Only send the ability packet if the shape equipped by the player has one
		LivingEntity shape = PlayerShape.getCurrentShape(client.player);

		if (shape != null) {
			if (AbilityRegistry.has(shape.getType())) {
				ClientNetworking.sendAbilityRequest();
			}
		}
	}

	private void handleTransformKey(Minecraft client) {
		// check if player is blacklisted
		if (SyncedVars.getPlayerBlacklist().contains(client.player.getUUID())) {
			client.player.displayClientMessage(Component.translatable("walkers.player_blacklisted"), true);
			return;
		}
		if (PlayerShape.getCurrentShape(client.player) == null)
			SwapPackets.sendSwapRequest(((PlayerDataProvider) client.player).get2ndShape());
		else
			SwapPackets.sendSwapRequest(null);
	}

	private void handleUnlockKey(Minecraft client) {
		// check if player is blacklisted
		if (SyncedVars.getPlayerBlacklist().contains(client.player.getUUID())) {
			client.player.displayClientMessage(Component.translatable("walkers.player_blacklisted"), true);
			return;
		}

		// check dev wolf
		if (((PlayerDataProvider) client.player).get2ndShape() != null && (client.player.isShiftKeyDown()
				&& (Walkers.devs.contains(client.player.getStringUUID()) || client.player.hasPermissions(2)))) {
			DevSwapPackets.sendDevSwapRequest(new ResourceLocation("minecraft:wolf"));
			return;
		}

		HitResult hit = client.hitResult;
		if ((((PlayerDataProvider) client.player).get2ndShape() == null || SyncedVars.getUnlockOveridesCurrentShape())
				&& hit instanceof EntityHitResult) {
			Entity entityHit = ((EntityHitResult) hit).getEntity();
			if (entityHit instanceof LivingEntity living) {
				@Nullable
				ShapeType<?> type = ShapeType.from(living);

				// Ensures, the mob isn't on the blacklist
				if (!SyncedVars.getShapeBlacklist().isEmpty()
						&& SyncedVars.getShapeBlacklist().contains(EntityType.getKey(type.getEntityType()).toString()))
					client.player.displayClientMessage(Component.translatable("walkers.unlock_entity_blacklisted"),
							true);
				else {
					if (currentTimer <= 0) {
						// unlock shape
						UnlockPackets.sendUnlockRequest(type);
						// send unlock message
						Component name = Component.translatable(type.getEntityType().getDescriptionId());
						client.player.displayClientMessage(Component.translatable("walkers.unlock_entity", name), true);
						currentTimer = SyncedVars.getUnlockTimer();
					} else {
						client.player.displayClientMessage(Component.translatable("walkers.unlock_progress"), true);
						currentTimer -= 1;
					}
				}
			}
		} else
			currentTimer = SyncedVars.getUnlockTimer();
	}
}
