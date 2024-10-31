package tocraft.walkers.impl.tick.shapes;

import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import tocraft.walkers.api.WalkersTickHandler;

public class SnowGolemTickHandler implements WalkersTickHandler<SnowGolem> {

    @Override
    public void tick(Player player, SnowGolem golem) {
        if (player.isCrouching() && player.onGround() && player.level().getBlockState(player.blockPosition()).isAir()) {
            BlockState defaultSnowBlockState = Blocks.SNOW.defaultBlockState();
            player.level().setBlockAndUpdate(player.blockPosition(), defaultSnowBlockState);
        }
    }
}
