package tocraft.walkers.mixin;

import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.impl.DimensionsRefresher;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "respawn", at = @At("RETURN"))
    private void onRespawn(ServerPlayer player, boolean alive, CallbackInfoReturnable<ServerPlayer> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        // refresh entity hitbox dimensions after death
        ((DimensionsRefresher) player).shape_refreshDimensions();

        if (shape != null) {
            // Re-sync max health for shapes
            if (Walkers.CONFIG.scalingHealth) {
                player.getAttribute(Attributes.MAX_HEALTH)
                        .setBaseValue(Math.min(Walkers.CONFIG.maxHealth, shape.getMaxHealth()));
                player.setHealth(player.getMaxHealth());
            }
            // sync max health & attack damage with clients
            if ((Walkers.CONFIG.scalingHealth || Walkers.CONFIG.percentScalingHealth)) {
                player.connection.send(new ClientboundUpdateAttributesPacket(player.getId(),
                        player.getAttributes().getSyncableAttributes()));
            }
        }
    }
}
