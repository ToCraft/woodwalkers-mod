package tocraft.walkers.ability.impl.specific;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.ShapeAbility;

public class WitherAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("wither");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WITHER_SHOOT, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        if (!world.isClientSide) {
            Vec3 lookDirection = player.getLookAngle();
            WitherSkull skull = new WitherSkull(world, player, new Vec3(lookDirection.x, lookDirection.y, lookDirection.z));
            skull.setPosRaw(player.getX(), player.getY() + 2, player.getZ());
            skull.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            world.addFreshEntity(skull);
        }
    }

    @Override
    public Item getIcon() {
        return Items.WITHER_SKELETON_SKULL;
    }

    @Override
    public int getDefaultCooldown() {
        return 200;
    }
}
