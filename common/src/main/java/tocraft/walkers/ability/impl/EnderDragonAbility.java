package tocraft.walkers.ability.impl;

import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.ability.WalkersAbility;

public class EnderDragonAbility extends WalkersAbility<EnderDragon> {

    @Override
    public void onUse(Player player, EnderDragon shape, Level world) {
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
