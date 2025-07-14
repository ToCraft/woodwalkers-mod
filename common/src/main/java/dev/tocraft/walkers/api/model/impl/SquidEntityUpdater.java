package dev.tocraft.walkers.api.model.impl;

import com.mojang.logging.LogUtils;
import dev.tocraft.walkers.api.model.EntityUpdater;
import dev.tocraft.walkers.mixin.accessor.SquidEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class SquidEntityUpdater<S extends Squid> implements EntityUpdater<S> {

    @Override
    public void update(Player player, @NotNull S squid) {
        squid.xBodyRotO = squid.xBodyRot;
        squid.zBodyRotO = squid.zBodyRot;
        squid.oldTentacleMovement = squid.tentacleMovement;
        squid.oldTentacleAngle = squid.tentacleAngle;
        float currentTentacleSpeed = ((SquidEntityAccessor) squid).getTentacleSpeed();
        squid.tentacleMovement += currentTentacleSpeed;
        if (squid.tentacleMovement > Math.PI * 2) {
            squid.tentacleMovement = (float) Math.PI * 2;
            if (squid.getRandom().nextInt(10) == 0) {
                ((SquidEntityAccessor) squid).setTentacleSpeed(1.0F / (squid.getRandom().nextFloat() + 1.0F) * 0.2F);
            }
        }
        if (player.isInWater()) {
            Vec3 playerDeltaMovement = player.getDeltaMovement();
            squid.yBodyRot = squid.yBodyRot + (-((float)Mth.atan2(playerDeltaMovement.x, playerDeltaMovement.z)) * (180.0F / (float)Math.PI) - squid.yBodyRot) * 0.1F;
            squid.xBodyRot = Mth.approachDegrees(squid.xBodyRot, -player.getXRot() - 90F, 10.0F);

        } else {
            squid.tentacleAngle = Mth.abs(Mth.sin(squid.tentacleMovement)) * (float) Math.PI * 0.25F;
            squid.xBodyRot = Mth.approachDegrees(squid.xBodyRot, - 90.0F, 2.0F);
        }
    }
}
