//#if MC>1194
package tocraft.walkers.ability.impl.specific;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import tocraft.craftedcore.patched.CEntity;
import tocraft.craftedcore.patched.Identifier;
import tocraft.walkers.ability.ShapeAbility;

import java.util.ArrayList;
import java.util.List;

public class SnifferAbility<T extends LivingEntity> extends ShapeAbility<T> {

    @Override
    public void onUse(Player player, T shape, Level world) {
        // Ensures, the player isn't in Water/Lava and touches the ground
        if (player.isInLava() || player.isInWater() || !CEntity.isOnGround(player))
            return;

        BlockPos playerPos = player.blockPosition();
        BlockPos blockPos = new BlockPos(playerPos.getX(), playerPos.getY() - 1, playerPos.getZ());
        List<Block> diggableBlocks = new ArrayList<>();
        diggableBlocks.add(BuiltInRegistries.BLOCK.get(Identifier.parse("minecraft:dirt")));
        diggableBlocks.add(BuiltInRegistries.BLOCK.get(Identifier.parse("minecraft:grass_block")));
        diggableBlocks.add(BuiltInRegistries.BLOCK.get(Identifier.parse("minecraft:podzol")));
        diggableBlocks.add(BuiltInRegistries.BLOCK.get(Identifier.parse("minecraft:rooted_dirt")));
        diggableBlocks.add(BuiltInRegistries.BLOCK.get(Identifier.parse("minecraft:moss_block")));
        diggableBlocks.add(BuiltInRegistries.BLOCK.get(Identifier.parse("minecraft:mud")));
        diggableBlocks.add(BuiltInRegistries.BLOCK.get(Identifier.parse("minecraft:muddy_mangrove_roots")));

        // checks, if the block bellow the player is in the 'diggableBlocks'-List
        if (diggableBlocks.contains(world.getBlockState(blockPos).getBlock()) && Math.random() <= 0.5D) {
            // drop Item
            if (getRandomBoolean())
                player.spawnAtLocation(Items.TORCHFLOWER_SEEDS);
            else
                player.spawnAtLocation(Items.PITCHER_POD);

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
//#endif
