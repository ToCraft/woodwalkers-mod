package tocraft.walkers.ability.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import tocraft.walkers.ability.ShapeAbility;

public class EvokerAbility<T extends Mob> extends ShapeAbility<T> {

    @Override
    public void onUse(Player player, T shape, Level world) {
        // spawn vexes while sneaking
        if (player.isCrouching() && world instanceof ServerLevel serverLevel) {
            int i = 0;
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof Vex && player.distanceTo(entity) <= 16)
                    ++i;
                if (i >= 8)
                    return;
            }

            for (int j = 0; j <= 2; j++) {
                Vex vex = new Vex(EntityType.VEX, world);
                vex.setPos(player.position());
                world.addFreshEntity(vex);
            }
        } else {
            // Spawn 8 Evoker Fangs out from the player.
            Vec3 origin = player.position();
            Vec3 facing = player.getLookAngle().multiply(1, 0, 1); // fangs should not go up/down based on pitch

            // Iterate out 5 blocks
            for (int blockOut = 0; blockOut < 8; blockOut++) {
                origin = origin.add(facing); // we add at the start -- no need to put a fang directly underneath the player!

                // Spawn an Evoker Fang at the given position.
                // For each position, we go up or down at most -+1 block per iteration.
                // If we cannot go up or down 1 block (or stay at the same level), the chain ends.

                // If the block underneath is solid, we are good to go.
                EvokerFangs fangs = new EvokerFangs(world, origin.x(), origin.y(), origin.z(), player.getYRot(), blockOut * 2, player);
                BlockPos underneathPosition = BlockPos.containing(origin).below();
                BlockState underneath = world.getBlockState(underneathPosition);
                if (underneath.isFaceSturdy(world, underneathPosition, Direction.UP) && world.isEmptyBlock(underneathPosition.above())) {
                    world.addFreshEntity(fangs);
                    continue;
                }

                // Check underneath (2x down) again...
                BlockPos underneath2Position = BlockPos.containing(origin).below(2);
                BlockState underneath2 = world.getBlockState(underneath2Position);
                if (underneath2.isFaceSturdy(world, underneath2Position, Direction.UP) && world.isEmptyBlock(underneath2Position.above())) {
                    fangs.setPosRaw(fangs.getX(), fangs.getY() - 1, fangs.getZ());
                    world.addFreshEntity(fangs);
                    origin = origin.add(0, -1, 0);
                    continue;
                }

                // Check above (1x up)
                BlockPos upPosition = BlockPos.containing(origin).above();
                BlockState up = world.getBlockState(underneath2Position);
                if (up.isFaceSturdy(world, upPosition, Direction.UP) && world.isEmptyBlock(upPosition)) {
                    fangs.setPosRaw(fangs.getX(), fangs.getY() + 1, fangs.getZ());
                    world.addFreshEntity(fangs);
                    origin = origin.add(0, 1, 0);
                    continue;
                }

                break;
            }
        }
    }

    @Override
    public Item getIcon() {
        return Items.EMERALD;
    }

    @Override
    public int getDefaultCooldown() {
        return 10;
    }
}
