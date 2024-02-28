package tocraft.walkers.ability.impl;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.ShapeAbility;

public class EnderDragonAbility<T extends Mob> extends ShapeAbility<T> {

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
