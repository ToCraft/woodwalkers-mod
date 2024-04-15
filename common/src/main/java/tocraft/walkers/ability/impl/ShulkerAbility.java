package tocraft.walkers.ability.impl;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.mixin.accessor.ShulkerAccessor;

public class ShulkerAbility<T extends Shulker> extends ShapeAbility<T> {

    @Override
    public void onUse(Player player, T shape, Level world) {
        LivingEntity target = player.level().getNearestEntity(player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(20, 4.0, 20), livingEntity -> true), TargetingConditions.forCombat().range(20).selector((livingEntity) -> !livingEntity.is(player)), player, player.getX(), player.getEyeY(), player.getZ());

        player.level().addFreshEntity(new ShulkerBullet(player.level(), player, target, player.getDirection().getAxis()));
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
