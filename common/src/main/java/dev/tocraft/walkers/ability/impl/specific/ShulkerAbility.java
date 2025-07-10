package dev.tocraft.walkers.ability.impl.specific;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.ability.ShapeAbility;
import dev.tocraft.walkers.mixin.accessor.ShulkerAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

@SuppressWarnings("resource")
public class ShulkerAbility<T extends Shulker> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("shulker");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        LivingEntity target = world.getNearestEntity(player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(20, 4.0, 20), livingEntity -> true), TargetingConditions.forCombat().range(20).selector((livingEntity, level) -> !livingEntity.is(player)), player, player.getX(), player.getEyeY(), player.getZ());

        if (target != null) {
            player.level().addFreshEntity(new ShulkerBullet(player.level(), player, target, player.getDirection().getAxis()));
        }
        player.playSound(SoundEvents.SHULKER_SHOOT, 2.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);

        ((ShulkerAccessor) shape).callSetRawPeekAmount(100);
    }

    @Override
    public Item getIcon() {
        return Items.SHULKER_SHELL;
    }

    @Override
    public int getDefaultCooldown() {
        return 80;
    }
}
