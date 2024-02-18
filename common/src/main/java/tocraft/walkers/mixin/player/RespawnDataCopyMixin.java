package tocraft.walkers.mixin.player;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.impl.DimensionsRefresher;
import tocraft.walkers.impl.PlayerDataProvider;

@Mixin(ServerPlayer.class)
public class RespawnDataCopyMixin {

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void copyWalkersData(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        PlayerDataProvider oldData = ((PlayerDataProvider) oldPlayer);
        PlayerDataProvider newData = ((PlayerDataProvider) this);

        // Transfer data from the old ServerPlayer -> new ServerPlayer
        newData.walkers$setAbilityCooldown(oldData.walkers$getAbilityCooldown());
        newData.walkers$setRemainingHostilityTime(oldData.walkers$getRemainingHostilityTime());
        newData.walkers$setCurrentShape(oldData.walkers$getCurrentShape());
        newData.walkers$set2ndShape(oldData.walkers$get2ndShape());
        ((DimensionsRefresher) this).shape_refreshDimensions();

        PlayerShapeChanger.sync((ServerPlayer) (Object) this);
    }
}
