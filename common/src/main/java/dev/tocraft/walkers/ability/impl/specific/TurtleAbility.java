package dev.tocraft.walkers.ability.impl.specific;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.ability.ShapeAbility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TurtleAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("turtle");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, LivingEntity shape, ServerLevel world) {
        if (!player.isInWater() && player.onGround() && world.getBlockState(player.blockPosition()).isAir()) {
            BlockState turtlEggBlockstate = Blocks.TURTLE_EGG.defaultBlockState().setValue(TurtleEggBlock.EGGS, player.getRandom().nextInt(4) + 1);
            world.setBlock(player.blockPosition(), turtlEggBlockstate, 3);
            // play sound
            world.playSound(null, player.blockPosition(), SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
        }
    }

    @Override
    public Item getIcon() {
        return Items.TURTLE_EGG;
    }

    @Override
    public int getDefaultCooldown() {
        return 6000;
    }
}
