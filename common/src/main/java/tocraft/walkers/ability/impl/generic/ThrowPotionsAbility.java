package tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

import java.util.Arrays;
import java.util.List;

public class ThrowPotionsAbility<T extends Mob> extends GenericShapeAbility<T> {
    public static final List<Potion> VALID_POTIONS = Arrays.asList(Potions.HARMING, Potions.POISON, Potions.SLOWNESS, Potions.WEAKNESS);

    public static final ResourceLocation ID = Walkers.id("throw_potion");
    public static final Codec<ThrowPotionsAbility<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("potions", VALID_POTIONS.stream().map(BuiltInRegistries.POTION::getKey).toList()).forGetter(o -> o.validPotions.stream().map(BuiltInRegistries.POTION::getKey).toList())
    ).apply(instance, instance.stable(validPotions -> new ThrowPotionsAbility<>(validPotions.stream().map(BuiltInRegistries.POTION::get).toList()))));

    private final List<Potion> validPotions;

    public ThrowPotionsAbility() {
        this(VALID_POTIONS);
    }

    public ThrowPotionsAbility(List<Potion> validPotions) {
        this.validPotions = validPotions;
    }

    @Override
    public void onUse(Player player, T shape, Level world) {
        ThrownPotion potionEntity = new ThrownPotion(world, player);
        potionEntity.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), validPotions.get(world.random.nextInt(validPotions.size()))));
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
    public Codec<? extends GenericShapeAbility<?>> codec() {
        return CODEC;
    }
}
