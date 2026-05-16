package dev.tocraft.walkers.mixin.client;

import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.api.model.EntityUpdater;
import dev.tocraft.walkers.api.model.EntityUpdaters;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayer.class)
public class ClientPlayerMixin {
    @SuppressWarnings("unchecked")
    @Inject(method = "tick", at = @At("RETURN"))
    private void fixEntityAnimations(CallbackInfo ci) {
        AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;
        LivingEntity shape = PlayerShape.getCurrentShape(player);
        if (shape != null) {
            EntityUpdater<LivingEntity> entityUpdater = EntityUpdaters.getUpdater((EntityType<@NotNull LivingEntity>) shape.getType());
            if (entityUpdater != null) {
                entityUpdater.update(player, shape);
            }
        }
    }
}
