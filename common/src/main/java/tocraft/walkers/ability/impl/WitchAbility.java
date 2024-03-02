package tocraft.walkers.ability.impl;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import tocraft.walkers.ability.ShapeAbility;

import java.util.Arrays;
import java.util.List;

public class WitchAbility<T extends Mob> extends ShapeAbility<T> {

    public static final List<Potion> VALID_POTIONS = Arrays.asList(Potions.HARMING, Potions.POISON, Potions.SLOWNESS, Potions.WEAKNESS);

    @Override
    public void onUse(Player player, T shape, Level world) {
        ThrownPotion potionEntity = new ThrownPotion(world, player);
        potionEntity.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), VALID_POTIONS.get(world.random.nextInt(VALID_POTIONS.size()))));
        potionEntity.setXRot(-20.0F);
        Vec3 rotation = player.getLookAngle();
        potionEntity.shoot(rotation.x(), rotation.y(), rotation.z(), 0.75F, 8.0F);

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WITCH_THROW, player.getSoundSource(), 1.0F, 0.8F + world.random.nextFloat() * 0.4F);

        world.addFreshEntity(potionEntity);
    }

    @Override
    public Item getIcon() {
        return Items.POTION;
    }

    @Override
    public int getDefaultCooldown() {
        return 200;
    }
}
