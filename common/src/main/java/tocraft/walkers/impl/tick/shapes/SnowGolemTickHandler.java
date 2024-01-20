package tocraft.walkers.impl.tick.shapes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import tocraft.walkers.api.WalkersTickHandler;

public class SnowGolemTickHandler implements WalkersTickHandler<SnowGolem> {

    @Override
    public void tick(Player player, SnowGolem golem) {
        if (player.isCrouching() && player.onGround() && player.level().getBlockState(player.blockPosition()).isAir()) {
            BlockState defaultSnowBlockState = BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft:snow")).defaultBlockState();
            player.level().setBlockAndUpdate(player.blockPosition(), defaultSnowBlockState);
        }
    }
}
