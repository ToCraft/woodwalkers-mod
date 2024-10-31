package tocraft.walkers.ability.impl.specific;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;

public class RabbitAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("rabbit");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        if (!world.isClientSide()) {
            BlockPos blockPos = player.blockPosition().above();
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (block instanceof CarrotBlock) {
                int i = blockState.getValue(CarrotBlock.AGE);
                if (i == 0) {
                    world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 2);
                    world.destroyBlock(blockPos, true, player);
                } else {
                    world.setBlock(blockPos, blockState.setValue(CarrotBlock.AGE, i - 1), 2);
                    world.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
                    world.levelEvent(player, 2001, blockPos, Block.getId(blockState));
                    player.gameEvent(GameEvent.EAT);
                    player.getFoodData().eat(1, 0.1F);
                }
            }
            world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.RABBIT_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Override
    public int getDefaultCooldown() {
        return 40;
    }

    @Override
    public Item getIcon() {
        return Items.CARROT;
    }
}
