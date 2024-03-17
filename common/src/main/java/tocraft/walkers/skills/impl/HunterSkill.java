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
import java.util.function.Predicate;
import java.util.stream.Stream;

public class HunterSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("hunter");
    public static final Codec<HunterSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).fieldOf("prey").forGetter(o -> new ArrayList<>())
    ).apply(instance, instance.stable(preyLocations -> {
        List<EntityType<?>> preyTypes = new ArrayList<>();
        for (ResourceLocation resourceLocation : preyLocations) {
            if (BuiltInRegistries.ENTITY_TYPE.containsKey(resourceLocation)) {
                preyTypes.add(BuiltInRegistries.ENTITY_TYPE.get(resourceLocation));
            }
        }
        return ofPreyType(preyTypes.toArray(EntityType[]::new));
    })));

    public final List<Predicate<LivingEntity>> prey;

    public static HunterSkill<?> ofPreyType(EntityType<?>... prey) {
        return new HunterSkill<>(Stream.of(prey).map(entry -> (Predicate<LivingEntity>) entity -> entity.getType().equals(entry)).toList());
    }

    @SafeVarargs
    public static HunterSkill<?> ofPreyClass(Class<? extends LivingEntity>... prey) {
        return new HunterSkill<>(Stream.of(prey).map(entry -> (Predicate<LivingEntity>) entry::isInstance).toList());
    }

    public HunterSkill(List<Predicate<LivingEntity>> prey) {
        this.prey = prey;
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
