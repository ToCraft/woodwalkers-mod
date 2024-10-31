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
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;

import java.util.ArrayList;
import java.util.List;

public class SnifferAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("sniffer");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        // Ensures, the player isn't in Water/Lava and touches the ground
        if (player.isInLava() || player.isInWater() || !player.onGround())
            return;

        BlockPos playerPos = player.blockPosition();
        BlockPos blockPos = new BlockPos(playerPos.getX(), playerPos.getY() - 1, playerPos.getZ());
        List<Block> diggableBlocks = new ArrayList<>();
        diggableBlocks.add(Blocks.DIRT);
        diggableBlocks.add(Blocks.GRASS_BLOCK);
        diggableBlocks.add(Blocks.PODZOL);
        diggableBlocks.add(Blocks.ROOTED_DIRT);
        diggableBlocks.add(Blocks.MOSS_BLOCK);
        diggableBlocks.add(Blocks.MUD);
        diggableBlocks.add(Blocks.MUDDY_MANGROVE_ROOTS);

        // checks, if the block bellow the player is in the 'diggableBlocks'-List
        if (diggableBlocks.contains(world.getBlockState(blockPos).getBlock()) && Math.random() <= 0.5D) {
            // drop Item
            if (getRandomBoolean())
                player.spawnAtLocation(world, Items.TORCHFLOWER_SEEDS);
            else
                player.spawnAtLocation(world, Items.PITCHER_POD);

            world.playSound(null, player, SoundEvents.SNIFFER_DIGGING, SoundSource.PLAYERS, 1.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
        } else
            world.playSound(null, player, SoundEvents.SNIFFER_DIGGING_STOP, SoundSource.PLAYERS, 1.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.TORCHFLOWER;
    }

    private static boolean getRandomBoolean() {
        return Math.random() < 0.5;
    }

    @Override
    public int getDefaultCooldown() {
        return 9600;
    }
}
