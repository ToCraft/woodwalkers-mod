package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

public class ReinforcementsSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("reinforcements");
    public static final Codec<ReinforcementsSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.optionalFieldOf("range", 32).forGetter(o -> o.range)
    ).apply(instance, instance.stable(ReinforcementsSkill::new)));
    public final int range;

    public ReinforcementsSkill() {
        this(32);
    }

    public ReinforcementsSkill(int range) {
        this.range = range;
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
