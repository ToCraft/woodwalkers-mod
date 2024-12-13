package tocraft.walkers.traits.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.traits.ShapeTrait;

import java.util.Optional;

public class AquaticTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("aquatic");
    public static final MapCodec<AquaticTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.INT.optionalFieldOf("is_aquatic").forGetter(o -> Optional.empty()),
            Codec.BOOL.optionalFieldOf("isAquatic", true).forGetter(o -> o.isAquatic),
            Codec.BOOL.optionalFieldOf("isLand", false).forGetter(o -> o.isLand)
    ).apply(instance, instance.stable((i, isAquatic, isLand) -> {
        if (i.isPresent()) {
            switch (i.get()) {
                case 0 -> {
                    return new AquaticTrait<>(true, false);
                }
                case 1 -> {
                    return new AquaticTrait<>(true, true);
                }
                case 2 -> {
                    return new AquaticTrait<>(false, true);
                }
            }
        }
        return new AquaticTrait<>(isAquatic, isLand);
    })));

    public final boolean isAquatic;
    public final boolean isLand;

    public AquaticTrait(boolean isAquatic, boolean isLand) {
        this.isAquatic = isAquatic;
        this.isLand = isLand;
    }

    public AquaticTrait(boolean isAquatic) {
        this(isAquatic, !isAquatic);
    }

    public AquaticTrait() {
        this(true, false);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }

    @Contract(pure = true)
    @Environment(EnvType.CLIENT)
    @Override
    public @Nullable Item getItemIcon() {
        return Items.HEART_OF_THE_SEA;
    }
}
