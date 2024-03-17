package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

public class AquaticSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("aquatic");
    public static final Codec<AquaticSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.optionalFieldOf("is_aquatic", 0).forGetter(o -> o.isAquatic)
    ).apply(instance, instance.stable(AquaticSkill::new)));

    public final int isAquatic;

    /**
     * @param isAquatic 0 - water mob, 1 - land and water mob, 2 - land mob
     */
    public AquaticSkill(int isAquatic) {
        this.isAquatic = isAquatic;
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
