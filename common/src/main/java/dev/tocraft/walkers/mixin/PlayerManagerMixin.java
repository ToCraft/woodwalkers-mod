package dev.tocraft.walkers.mixin;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.api.PlayerShapeChanger;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "respawn", at = @At("RETURN"))
    private void onRespawn(ServerPlayer player, boolean bl, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayer> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        // refresh entity hitbox dimensions after death
        player.refreshDimensions();

        if (shape != null) {
            // Re-sync max health for shape
            if (Walkers.CONFIG.scalingHealth) {
                player.getAttribute(Attributes.MAX_HEALTH)
                        .setBaseValue(Math.min(Walkers.CONFIG.maxHealth, shape.getMaxHealth()));
                player.setHealth(player.getMaxHealth());
            }
            // Re-sync amor for shape
            if (Walkers.CONFIG.scalingAmor) {
                player.getAttribute(Attributes.ARMOR)
                        .setBaseValue(Math.min(Walkers.CONFIG.maxAmor, shape.getAttributeBaseValue(Attributes.ARMOR)));
                player.getAttribute(Attributes.ARMOR_TOUGHNESS)
                        .setBaseValue(Math.min(Walkers.CONFIG.maxAmorToughness, shape.getAttributeBaseValue(Attributes.ARMOR_TOUGHNESS)));
            }
            // Re-sync scale for shape
            AttributeInstance playerScaleAttribute = player.getAttribute(Attributes.SCALE);
            AttributeInstance shapeScaleAttribute = shape.getAttribute(Attributes.SCALE);
            if (playerScaleAttribute != null && shapeScaleAttribute != null) {
                shapeScaleAttribute.setBaseValue(playerScaleAttribute.getBaseValue());
            }
            // Re-sync step height for shape
            if (Walkers.CONFIG.scalingStepHeight) {
                player.getAttribute(Attributes.STEP_HEIGHT)
                        .setBaseValue(shape.getAttributeBaseValue(Attributes.STEP_HEIGHT));
            }
            // sync max health & attack damage with clients
            player.connection.send(new ClientboundUpdateAttributesPacket(player.getId(),
                    player.getAttributes().getSyncableAttributes()));
        }

        // send sync packets
        PlayerShapeChanger.sync(player);
        PlayerShape.sync(player);
    }
}
