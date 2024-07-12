package tocraft.walkers.impl.tick.shapes;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import tocraft.craftedcore.patched.CEntity;
import tocraft.craftedcore.patched.CRegistries;
import tocraft.craftedcore.patched.Identifier;
import tocraft.walkers.api.WalkersTickHandler;

public class SnowGolemTickHandler implements WalkersTickHandler<SnowGolem> {

    @SuppressWarnings("unchecked")
    @Override
    public void tick(Player player, SnowGolem golem) {
        if (player.isCrouching() && CEntity.isOnGround(player) && CEntity.level(player).getBlockState(player.blockPosition()).isAir()) {
            BlockState defaultSnowBlockState = ((Registry<Block>) CRegistries.getRegistry(Identifier.parse("block"))).get(Identifier.parse("minecraft:snow")).defaultBlockState();
            CEntity.level(player).setBlockAndUpdate(player.blockPosition(), defaultSnowBlockState);
        }
    }
}
