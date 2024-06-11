package tocraft.walkers.ability.impl.generic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.GenericShapeAbility;

public class SaturateAbility<T extends Mob> extends GenericShapeAbility<T> {
    public static final ResourceLocation ID = Walkers.id("saturate");
    public static final MapCodec<SaturateAbility<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.INT.optionalFieldOf("food_level", 6).forGetter(o -> o.foodLevel),
            Codec.FLOAT.optionalFieldOf("saturation_level", 0.1F).forGetter(o -> o.saturationLevel)
    ).apply(instance, instance.stable(SaturateAbility::new)));

    private final int foodLevel;
    private final float saturationLevel;

    public SaturateAbility() {
        this(6, 0.1F);
    }

    public SaturateAbility(int foodLevel, float saturationLevel) {
        this.foodLevel = foodLevel;
        this.saturationLevel = saturationLevel;
    }

    @Override
    public void onUse(Player player, T shape, Level world) {
        player.getFoodData().eat(foodLevel, saturationLevel);

        world.playSound(null, player, SoundEvents.SHEEP_STEP, SoundSource.PLAYERS, 1.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.MUSHROOM_STEW;
    }

    @Override
    public int getDefaultCooldown() {
        return 300;
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
