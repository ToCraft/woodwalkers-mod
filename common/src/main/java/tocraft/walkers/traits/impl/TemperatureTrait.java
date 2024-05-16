package tocraft.walkers.traits.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.traits.ShapeTrait;

public class TemperatureTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("temperature");
    public static final Codec<TemperatureTrait<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("cold_enough_to_snow", true).forGetter(o -> o.coldEnoughToSnow)
    ).apply(instance, instance.stable(TemperatureTrait::new)));

    public final boolean coldEnoughToSnow;


    public TemperatureTrait() {
        this(true);
    }

    /**
     * Damage the player if they aren't in an area which is  cold enough to snow (or warm enough to rain, if "coldEnoughToSnow" is "false")
     */
    public TemperatureTrait(boolean coldEnoughToSnow) {
        this.coldEnoughToSnow = coldEnoughToSnow;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }
}
