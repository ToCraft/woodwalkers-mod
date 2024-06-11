package tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

public class ShootDragonFireball<T extends Mob> extends GenericShapeAbility<T> {
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
    public void onUse(Player player, T shape, Level world) {
        DragonFireball dragonFireball = new DragonFireball(
                world,
                player,
                player.getLookAngle().x,
                player.getLookAngle().y,
                player.getLookAngle().z
        );

        dragonFireball.setOwner(player);
        world.addFreshEntity(dragonFireball);
    }

    @Override
    public Item getIcon() {
        return Items.DRAGON_BREATH;
    }
}
