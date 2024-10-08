package tocraft.walkers.mixin.client.player;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.craftedcore.patched.client.CMinecraft;
import tocraft.walkers.impl.PlayerDataProvider;

import java.util.Optional;
import java.util.UUID;

@Environment(EnvType.CLIENT)
//#if MC>1182
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {

    public AbstractClientPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
       super(level, pos, yRot, gameProfile);
   }
//#else
//$$ @Mixin(Player.class)
//$$ public abstract class AbstractClientPlayerMixin extends LivingEntity {
//$$     protected AbstractClientPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
//$$         super(entityType, level);
//$$     }
//$$
//#endif
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At("HEAD"))
    private void clientTick(CallbackInfo info) {
        Optional<UUID> vehiclePlayerId = ((PlayerDataProvider) this).walkers$getVehiclePlayerUUID();
        if (vehiclePlayerId.isPresent() && CMinecraft.isLocalPlayer(vehiclePlayerId.get())) {
            Vec3 vehiclePos = Minecraft.getInstance().player.position();
            this.moveTo(vehiclePos.x, vehiclePos.y + 1, vehiclePos.z);
        }
    }
}
