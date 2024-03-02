package tocraft.walkers.api.model.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import tocraft.walkers.api.model.EntityUpdater;
import tocraft.walkers.mixin.accessor.SquidEntityAccessor;

@Environment(EnvType.CLIENT)
public class SquidEntityUpdater<S extends Squid> implements EntityUpdater<S> {
    @Override
    public void update(Player player, S squid) {
        squid.xBodyRotO = squid.xBodyRot;
        squid.zBodyRotO = squid.zBodyRot;
        squid.oldTentacleMovement = squid.tentacleMovement;
        squid.oldTentacleAngle = squid.tentacleAngle;
        squid.tentacleMovement += 1.0F / (squid.getRandom().nextFloat() + 1.0F) * 0.2F;
        if ((double) squid.tentacleMovement > 6.283185307179586) {
            squid.tentacleMovement = 6.2831855F;
        }

        if (player.isInWaterOrBubble()) {
            if (squid.tentacleMovement < 3.1415927F) {
                float f = squid.tentacleMovement / 3.1415927F;
                squid.tentacleAngle = Mth.sin(f * f * 3.1415927F) * 3.1415927F * 0.25F;
                if ((double) f > 0.75) {
                    ((SquidEntityAccessor) squid).setSpeed(1.0F);
                    ((SquidEntityAccessor) squid).setRotateSpeed(1.0F);
                } else {
                    ((SquidEntityAccessor) squid).setRotateSpeed(((SquidEntityAccessor) squid).getRotateSpeed() * 0.8F);
                }
            } else {
                squid.tentacleAngle = 0.0F;
                ((SquidEntityAccessor) squid).setSpeed(((SquidEntityAccessor) squid).getSpeed() * 0.9F);
                ((SquidEntityAccessor) squid).setRotateSpeed(((SquidEntityAccessor) squid).getRotateSpeed() * 0.99F);
            }

            Vec3 vec3 = player.getDeltaMovement();
            double d = vec3.horizontalDistance();
            squid.zBodyRot += 3.1415927F * ((SquidEntityAccessor) squid).getRotateSpeed() * 1.5F;
            squid.xBodyRot += (-((float) Mth.atan2(d, vec3.y)) * 57.295776F - squid.xBodyRot) * 0.1F;
        } else {
            squid.tentacleAngle = Mth.abs(Mth.sin(squid.tentacleMovement)) * 3.1415927F * 0.25F;
            squid.xBodyRot += (-90.0F - squid.xBodyRot) * 0.02F;
        }
    }
}
