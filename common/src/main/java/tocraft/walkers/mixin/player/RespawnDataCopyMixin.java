package tocraft.walkers.mixin.player;

import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.impl.PlayerDataProvider;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class RespawnDataCopyMixin {

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void copyWalkersData(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        PlayerDataProvider oldData = ((PlayerDataProvider) oldPlayer);
        PlayerDataProvider newData = ((PlayerDataProvider) this);

        // Transfer data from the old ServerPlayer -> new ServerPlayer
        newData.setAbilityCooldown(oldData.getAbilityCooldown());
        newData.setRemainingHostilityTime(oldData.getRemainingHostilityTime());
        newData.setCurrentShape(oldData.getCurrentShape());
        newData.set2ndShape(oldData.get2ndShape());

        PlayerShapeChanger.sync((ServerPlayer) (Object) this);
    }
}
