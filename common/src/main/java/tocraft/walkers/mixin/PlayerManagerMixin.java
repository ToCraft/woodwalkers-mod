package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.event.PlayerJoinCallback;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.impl.DimensionsRefresher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

   @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;onPlayerConnected(Lnet/minecraft/server/network/ServerPlayerEntity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void connect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PlayerJoinCallback.EVENT.invoker().onPlayerJoin(player);
    }

    @Inject(
            method = "respawnPlayer",
            at = @At("RETURN")
    )
    private void onRespawn(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        // refresh entity hitbox dimensions after death
        ((DimensionsRefresher) player).shape_refreshDimensions();

        if(shape != null) {
            // Re-sync max health for shapes
            if (WalkersConfig.getInstance().scalingHealth()) {
                player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.min(WalkersConfig.getInstance().maxHealth(), shape.getMaxHealth()));
                player.setHealth(player.getMaxHealth());
                }
            // Re-sync attack damage for shapes
            if (WalkersConfig.getInstance().scalingAttackDamage()) {
                // get shape attack damage, return 1D if value is lower then max or not existing
                Double shapeAttackDamage = 1D;
                try {
                    shapeAttackDamage = Math.max(shape.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getBaseValue(), shapeAttackDamage);
                }
                catch(Exception ignored) {
                }
                player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.min(WalkersConfig.getInstance().maxAttackDamage(), shapeAttackDamage));
            }
            // sync max health & attack damage with clients
            if ((WalkersConfig.getInstance().scalingHealth() || WalkersConfig.getInstance().scalingAttackDamage()) || WalkersConfig.getInstance().scalingAttackDamage()) {
                player.networkHandler.sendPacket(new EntityAttributesS2CPacket(player.getId(), player.getAttributes().getAttributesToSend()));
            }
        }
    }
}
