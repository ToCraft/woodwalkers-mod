package tocraft.walkers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.event.PlayerJoinCallback;
import tocraft.walkers.impl.DimensionsRefresher;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {

	@Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addNewPlayer(Lnet/minecraft/server/level/ServerPlayer;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void connect(Connection connection, ServerPlayer player, CallbackInfo ci) {
		PlayerJoinCallback.EVENT.invoker().onPlayerJoin(player);
	}

	@Inject(method = "respawn", at = @At("RETURN"))
	private void onRespawn(ServerPlayer player, boolean alive, CallbackInfoReturnable<ServerPlayer> cir) {
		LivingEntity shape = PlayerShape.getCurrentShape(player);

		// refresh entity hitbox dimensions after death
		((DimensionsRefresher) player).shape_refreshDimensions();

		if (shape != null) {
			// Re-sync max health for shapes
			if (Walkers.CONFIG.scalingHealth()) {
				player.getAttribute(Attributes.MAX_HEALTH)
						.setBaseValue(Math.min(Walkers.CONFIG.maxHealth(), shape.getMaxHealth()));
				player.setHealth(player.getMaxHealth());
			}
			// Re-sync attack damage for shapes
			if (Walkers.CONFIG.scalingAttackDamage()) {
				// get shape attack damage, return 1D if value is lower then max or not existing
				Double shapeAttackDamage = 1D;
				try {
					shapeAttackDamage = Math.max(shape.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue(),
							shapeAttackDamage);
				} catch (Exception ignored) {
				}
				player.getAttribute(Attributes.ATTACK_DAMAGE)
						.setBaseValue(Math.min(Walkers.CONFIG.maxAttackDamage(), shapeAttackDamage));
			}
			// sync max health & attack damage with clients
			if ((Walkers.CONFIG.scalingHealth() || Walkers.CONFIG.scalingAttackDamage())
					|| Walkers.CONFIG.scalingAttackDamage()) {
				player.connection.send(new ClientboundUpdateAttributesPacket(player.getId(),
						player.getAttributes().getSyncableAttributes()));
			}
		}
	}
}
