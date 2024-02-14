package tocraft.walkers.ability.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import tocraft.walkers.ability.ShapeAbility;

public class GrassEaterAbility<T extends Mob> extends ShapeAbility<T> {
    SoundEvent sound;

    public GrassEaterAbility(SoundEvent sound) {
        this.sound = sound;
    }

    @Override
    public void onUse(Player player, T shape, Level world) {
        this.eatGrass(player, world);
    }

    protected void eatGrass(Player player, Level world) {
        BlockPos playerPos = player.blockPosition();
        BlockPos blockPos = new BlockPos(playerPos.getX(), playerPos.getY() - 1, playerPos.getZ());

        if ((world.getBlockState(playerPos).getBlock() == BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft:grass")) || world.getBlockState(playerPos).getBlock() == BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft:tall_grass")))) {
            BlockState defaultAirBlockState = BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft:air")).defaultBlockState();
            world.setBlockAndUpdate(playerPos, defaultAirBlockState);
            player.getFoodData().eat(2, 0.1F);
        } else if (world.getBlockState(blockPos).getBlock() == BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft:grass_block"))) {
            BlockState defaultDirtBlockState = BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft:dirt")).defaultBlockState();
            world.setBlockAndUpdate(blockPos, defaultDirtBlockState);
            player.getFoodData().eat(3, 0.1F);
        }

        world.playSound(null, player, sound, SoundSource.PLAYERS, 1.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public Item getIcon() {
        return BuiltInRegistries.ITEM.get(new ResourceLocation("grass"));
    }
}
