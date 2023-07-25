package tocraft.walkers.ability.impl;

import tocraft.walkers.ability.WalkersAbility;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SnifferAbility extends WalkersAbility<SnifferEntity> {

    @Override
    public void onUse(PlayerEntity player, SnifferEntity shape, World world) {
        // Ensures, the player isn't in Water/Lava and touches the ground
        if (player.isInLava() || player.isTouchingWater() || !player.isOnGround())
            return;

        BlockPos playerPos = player.getBlockPos();
        BlockPos blockPos = new BlockPos(playerPos.getX(), playerPos.getY()-1, playerPos.getZ());
        List<Block> diggableBlocks = new ArrayList<>();
        diggableBlocks.add(Registries.BLOCK.get(new Identifier("minecraft:dirt")));
        diggableBlocks.add(Registries.BLOCK.get(new Identifier("minecraft:grass_block")));
        diggableBlocks.add(Registries.BLOCK.get(new Identifier("minecraft:podzol")));
        diggableBlocks.add(Registries.BLOCK.get(new Identifier("minecraft:rooted_dirt")));
        diggableBlocks.add(Registries.BLOCK.get(new Identifier("minecraft:moss_block")));
        diggableBlocks.add(Registries.BLOCK.get(new Identifier("minecraft:mud")));
        diggableBlocks.add(Registries.BLOCK.get(new Identifier("minecraft:muddy_mangrove_roots")));

        // checks, if the block bellow the player is in the 'diggableBlocks'-List
        if (diggableBlocks.contains(world.getBlockState(blockPos).getBlock()) && Math.random() <= 0.5D) {
            // drop Item
            if (getRandomBoolean())
                player.dropItem(Items.TORCHFLOWER_SEEDS);
            else
                player.dropItem(Items.PITCHER_POD);

            world.playSoundFromEntity(null, player, SoundEvents.ENTITY_SNIFFER_DIGGING, SoundCategory.PLAYERS, 1.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
        }
        else
            world.playSoundFromEntity(null, player, SoundEvents.ENTITY_SNIFFER_DIGGING_STOP, SoundCategory.PLAYERS, 1.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.TORCHFLOWER;
    }

    private static boolean getRandomBoolean() {
        return Math.random() < 0.5;
    }
}
