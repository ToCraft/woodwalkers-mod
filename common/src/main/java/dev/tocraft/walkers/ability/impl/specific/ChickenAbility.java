package dev.tocraft.walkers.ability.impl.specific;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.ability.ShapeAbility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ChickenAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("chicken");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        player.spawnAtLocation(world, Items.EGG);

        // Play SFX
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHICKEN_EGG, player.getSoundSource(), 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F);
    }

    @Override
    public Item getIcon() {
        return Items.EGG;
    }

    @Override
    public int getDefaultCooldown() {
        return 1200;
    }
}
