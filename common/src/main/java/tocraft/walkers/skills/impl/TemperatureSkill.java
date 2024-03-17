package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

public class TemperatureSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("temperature");
    public static final Codec<TemperatureSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("cold_enough_to_snow", true).forGetter(o -> o.coldEnoughToSnow)
    ).apply(instance, instance.stable(TemperatureSkill::new)));

    public final boolean coldEnoughToSnow;


    public TemperatureSkill() {
        this(true);
    }

    /**
     * Damage the player if they aren't in an area which is  cold enough to snow (or warm enough to rain, if "coldEnoughToSnow" is "false")
     */
    public TemperatureSkill(boolean coldEnoughToSnow) {
        this.coldEnoughToSnow = coldEnoughToSnow;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends ShapeSkill<?>> codec() {
        return CODEC;
    }
}
