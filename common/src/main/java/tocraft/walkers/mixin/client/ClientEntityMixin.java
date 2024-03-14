package tocraft.walkers.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.impl.PlayerDataProvider;

import java.util.Optional;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public abstract class ClientEntityMixin {
    @Inject(method = "getVehicle", at = @At("RETURN"), cancellable = true)
    private void getClientVehicle(CallbackInfoReturnable<Entity> cir) {
        if ((Object) this instanceof AbstractClientPlayer clientPlayer && cir.getReturnValue() == null) {
            Optional<UUID> vehiclePlayerID = ((PlayerDataProvider) clientPlayer).walkers$getVehiclePlayerUUID();
            if (vehiclePlayerID.isPresent() && Minecraft.getInstance().isLocalPlayer(vehiclePlayerID.get())) {
                cir.setReturnValue(Minecraft.getInstance().player);
            }
        }
    }
}
