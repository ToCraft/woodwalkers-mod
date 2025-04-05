package tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownSplashPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.Vec3;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

import java.util.Arrays;
import java.util.List;

public class ThrowPotionsAbility<T extends LivingEntity> extends GenericShapeAbility<T> {
    public static final List<Holder<Potion>> VALID_POTIONS = Arrays.asList(Potions.HARMING, Potions.POISON, Potions.SLOWNESS, Potions.WEAKNESS);
    private final List<Holder<Potion>> validPotions;

    public ThrowPotionsAbility(List<Holder<Potion>> validPotions) {
        this.validPotions = validPotions;
    }

    public static final ResourceLocation ID = Walkers.id("throw_potion");
    public static final MapCodec<ThrowPotionsAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("potions", VALID_POTIONS.stream().map(potion -> BuiltInRegistries.POTION.getKey(potion.value())).toList()).forGetter(o -> o.validPotions.stream().map(potion -> BuiltInRegistries.POTION.getKey(potion.value())).toList())
    ).apply(instance, instance.stable(validPotions -> new ThrowPotionsAbility<>(validPotions.stream().map(potionId -> (Holder<Potion>) BuiltInRegistries.POTION.get(potionId).orElseThrow()).toList()))));

    public ThrowPotionsAbility() {
        this(VALID_POTIONS);
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        ThrownSplashPotion potionEntity = new ThrownSplashPotion(world, player, PotionContents.createItemStack(Items.SPLASH_POTION, validPotions.get(world.random.nextInt(validPotions.size()))));
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

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends GenericShapeAbility<?>> codec() {
        return CODEC;
    }
}
