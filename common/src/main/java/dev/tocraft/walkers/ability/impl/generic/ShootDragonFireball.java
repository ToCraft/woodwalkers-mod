package dev.tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.ability.GenericShapeAbility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class ShootDragonFireball<T extends LivingEntity> extends GenericShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("shoot_dragon_fireball");
    public static final MapCodec<ShootDragonFireball<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.stable(new ShootDragonFireball<>()));

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends GenericShapeAbility<?>> codec() {
        return CODEC;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        DragonFireball dragonFireball = new DragonFireball(
                world,
                player,
                new Vec3(player.getLookAngle().x,
                        player.getLookAngle().y,
                        player.getLookAngle().z)
        );

        dragonFireball.setOwner(player);
        world.addFreshEntity(dragonFireball);
    }

    @Override
    public Item getIcon() {
        return Items.DRAGON_BREATH;
    }
}
