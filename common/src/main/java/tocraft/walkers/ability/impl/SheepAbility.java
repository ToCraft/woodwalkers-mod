package tocraft.walkers.ability.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tocraft.walkers.ability.WalkersAbility;

public class SheepAbility extends WalkersAbility<Sheep> {

    @Override
    public void onUse(Player player, Sheep shape, Level world) {
        BlockPos playerPos = player.blockPosition();
        BlockPos blockPos = new BlockPos(playerPos.getX(), playerPos.getY()-1, playerPos.getZ());

        if ((world.getBlockState(playerPos).getBlock() == BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft:grass")) || world.getBlockState(playerPos).getBlock() == BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft:tall_grass")))) {
            BlockState defaultAirBlockState =  BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft:air")).defaultBlockState();
            world.setBlockAndUpdate(playerPos, defaultAirBlockState);
            player.getFoodData().eat(2, 0.1F);
        }
        else if (world.getBlockState(blockPos).getBlock() == BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft:grass_block"))) {
            BlockState defaultDirtBlockState =  BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft:dirt")).defaultBlockState();
            world.setBlockAndUpdate(blockPos, defaultDirtBlockState);
            player.getFoodData().eat(3, 0.1F);
        }

        world.playSound(null, player, SoundEvents.SHEEP_STEP, SoundSource.PLAYERS, 1.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.GRASS;
    }
}
