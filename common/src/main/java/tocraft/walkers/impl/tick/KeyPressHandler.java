package tocraft.walkers.impl.tick;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import tocraft.craftedcore.events.client.ClientTickEvents;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.network.ClientNetworking;
import tocraft.walkers.network.impl.SpecialSwapPackets;
import tocraft.walkers.network.impl.SwapPackets;
import tocraft.walkers.network.impl.UnlockPackets;
import tocraft.walkers.registry.WalkersEntityTags;

public class KeyPressHandler implements ClientTickEvents.Client {
	private float currentTimer = 0f;

	@Override
	public void tick(Minecraft client) {
		assert client.player != null;

		if (WalkersClient.ABILITY_KEY.consumeClick())
			handleAbilityKey(client);

		if (WalkersClient.TRANSFORM_KEY.consumeClick()) {
			SwapPackets.sendSwapRequest();
		}
		
		if (WalkersClient.SPECIAL_TRANSFORM_KEY.consumeClick() && Walkers.hasSpecialShape(Minecraft.getInstance().player.getUUID())) {
			SpecialSwapPackets.sendSpecialSwapRequest();
		}

		if (WalkersClient.UNLOCK_KEY.isDown())
			handleUnlockKey(client);

		else if (currentTimer != Walkers.CONFIG.unlockTimer)
			currentTimer = Walkers.CONFIG.unlockTimer;
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

	private void handleUnlockKey(Minecraft client) {
		// check if player is blacklisted
		if (client.player != null && Walkers.CONFIG.playerUUIDBlacklist.contains(client.player.getUUID())) {
			client.player.displayClientMessage(Component.translatable("walkers.player_blacklisted"), true);
			return;
		}

		HitResult hit = client.hitResult;
		if (client.player != null && (((PlayerDataProvider) client.player).walkers$get2ndShape() == null || Walkers.CONFIG.unlockOveridesCurrentShape)
				&& hit instanceof EntityHitResult) {
			Entity entityHit = ((EntityHitResult) hit).getEntity();
			if (entityHit instanceof LivingEntity living) {
				@Nullable
				ShapeType<?> type = ShapeType.from(living);

				// Ensures, the mob isn't on the blacklist
				if (type.getEntityType().is(WalkersEntityTags.BLACKLISTED))
					client.player.displayClientMessage(Component.translatable("walkers.unlock_entity_blacklisted"),
							true);
				else {
					if (currentTimer <= 0) {
						// unlock shape
						UnlockPackets.sendUnlockRequest(type);
						// send unlock message
						Component name = Component.translatable(type.getEntityType().getDescriptionId());
						client.player.displayClientMessage(Component.translatable("walkers.unlock_entity", name), true);
						currentTimer = Walkers.CONFIG.unlockTimer;
					} else {
						client.player.displayClientMessage(Component.translatable("walkers.unlock_progress"), true);
						currentTimer -= 1;
					}
				}
			}
		} else
			currentTimer = Walkers.CONFIG.unlockTimer;
	}
}
