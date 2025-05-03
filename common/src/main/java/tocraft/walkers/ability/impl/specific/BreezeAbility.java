package tocraft.walkers.ability.impl.specific;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WindChargeItem;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;

public class BreezeAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("breeze");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        if (player.isShiftKeyDown()) {
            // use jump boost
            player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, this.getCooldown(shape), 5, true, false));
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BREEZE_JUMP, SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            // fire wind charge
            Projectile.spawnProjectileFromRotation(
                    (serverLevelx, livingEntity, itemStackx) -> new WindCharge(player, world, player.position().x(), player.getEyePosition().y(), player.position().z()),
                    world,
                    new ItemStack(Items.WIND_CHARGE),
                    player,
                    0.0F,
                    WindChargeItem.PROJECTILE_SHOOT_POWER,
                    1.0F
            );

            world.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.WIND_CHARGE_THROW,
                    SoundSource.NEUTRAL,
                    0.5F,
                    0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
            );
        }
    }

    @Override
    public Item getIcon() {
        return Items.WIND_CHARGE;
    }

    @Override
    public int getDefaultCooldown() {
        return 60;
    }
}
