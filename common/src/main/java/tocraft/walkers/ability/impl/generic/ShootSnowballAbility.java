package tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

public class ShootSnowballAbility<T extends LivingEntity> extends GenericShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("shoot_snowball");
    public static final MapCodec<ShootSnowballAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.stable(new ShootSnowballAbility<>()));

    @Override
    public void onUse(@NotNull ServerPlayer player, T shape, @NotNull ServerLevel world) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        if (!world.isClientSide) {
            for (int i = 0; i < 10; i++) {
                Snowball snowballEntity = EntityType.SNOWBALL.create(world, EntitySpawnReason.LOAD);
                if (snowballEntity != null) {
                    snowballEntity.setOwner(player);
                    snowballEntity.setItem(new ItemStack(Items.SNOWBALL));
                    snowballEntity.shootFromRotation(player, player.getXRot() + world.random.nextInt(10) - 5, player.getYRot() + world.random.nextInt(10) - 5, 0.0F, 1.5F, 1.0F);
                    world.addFreshEntity(snowballEntity);
                }
            }
        }
    }

    @Override
    public Item getIcon() {
        return Items.SNOWBALL;
    }

    @Override
    public int getDefaultCooldown() {
        return 10;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends GenericShapeAbility<?>> codec() {
        return CODEC;
    }
}
