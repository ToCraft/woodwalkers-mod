package dev.tocraft.walkers.ability.impl.specific;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.ability.ShapeAbility;
import dev.tocraft.walkers.impl.PlayerDataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class AnimationAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("animation");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        ((PlayerDataProvider) player).walkers$setIsSpecialAnim(!((PlayerDataProvider) player).walkers$getIsSpecialAnim());
    }

    @Override
    public Item getIcon() {
        return Items.GRASS_BLOCK;
    }

    @Override
    public int getDefaultCooldown() {
        return 10;
    }
}
