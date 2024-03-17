package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

import java.util.ArrayList;
import java.util.List;

public class ReinforcementsSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("reinforcements");
    public static final Codec<ReinforcementsSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.optionalFieldOf("range", 32).forGetter(o -> o.range),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("reinforcements", new ArrayList<>()).forGetter(o -> o.reinforcements.stream().map(BuiltInRegistries.ENTITY_TYPE::getKey).toList())
    ).apply(instance, instance.stable((range, reinforcementsLocation) -> {
        List<EntityType<?>> reinforcements = new ArrayList<>();
        for (ResourceLocation resourceLocation : reinforcementsLocation) {
            if (BuiltInRegistries.ENTITY_TYPE.containsKey(resourceLocation))
                reinforcements.add(BuiltInRegistries.ENTITY_TYPE.get(resourceLocation));
        }
        return new ReinforcementsSkill<>(range, reinforcements);
    })));
    public final int range;
    public final List<EntityType<?>> reinforcements;

    public ReinforcementsSkill() {
        this(32);
    }

    public ReinforcementsSkill(int range) {
        this(range, new ArrayList<>());
    }

    public ReinforcementsSkill(List<EntityType<?>> reinforcements) {
        this(32, reinforcements);
    }

    public ReinforcementsSkill(int range, List<EntityType<?>> reinforcements) {
        this.range = range;
        this.reinforcements = reinforcements;
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
