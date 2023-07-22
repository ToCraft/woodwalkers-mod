package tocraft.walkers.ability.impl;

import tocraft.walkers.ability.WalkersAbility;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GrassEaterAbility extends WalkersAbility<SheepEntity> {

    @Override
    public void onUse(PlayerEntity player, SheepEntity shape, World world) {
        BlockPos playerPos = player.getBlockPos();
        BlockPos blockPos = new BlockPos(playerPos.getX(), playerPos.getY()-1, playerPos.getZ());

        if ((world.getBlockState(playerPos).getBlock() == Registries.BLOCK.get(new Identifier("minecraft:grass")) || world.getBlockState(playerPos).getBlock() == Registries.BLOCK.get(new Identifier("minecraft:tall_grass")))) {
            BlockState defaultAirBlockState =  Registries.BLOCK.get(new Identifier("minecraft:air")).getDefaultState();
            world.setBlockState(playerPos, defaultAirBlockState);
            player.getHungerManager().add(2, 0.1F);
        }
        else if (world.getBlockState(blockPos).getBlock() == Registries.BLOCK.get(new Identifier("minecraft:grass_block"))) {
            BlockState defaultDirtBlockState =  Registries.BLOCK.get(new Identifier("minecraft:dirt")).getDefaultState();
            world.setBlockState(blockPos, defaultDirtBlockState);
            player.getHungerManager().add(3, 0.1F);
        }

        world.playSoundFromEntity(null, player, SoundEvents.ENTITY_SHEEP_STEP, SoundCategory.PLAYERS, 1.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.GRASS;
    }
}
