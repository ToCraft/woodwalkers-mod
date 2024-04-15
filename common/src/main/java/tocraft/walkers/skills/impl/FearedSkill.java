package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FearedSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("feared");
    public static final Codec<FearedSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).fieldOf("fearful").forGetter(o -> new ArrayList<>())
    ).apply(instance, instance.stable(preyLocations -> {
        List<Predicate<LivingEntity>> fearful = new ArrayList<>();
        for (ResourceLocation resourceLocation : preyLocations) {
            if (BuiltInRegistries.ENTITY_TYPE.containsKey(resourceLocation)) {
                fearful.add(entity -> entity.getType().equals(BuiltInRegistries.ENTITY_TYPE.get(resourceLocation)));
            } else {
                fearful.add(entity -> entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, resourceLocation)));
            }
        }
        return new FearedSkill<>(fearful);
    })));

    public final List<Predicate<LivingEntity>> fearful;

    public static FearedSkill<?> ofFearfulType(EntityType<?>... fearful) {
        return new FearedSkill<>(Stream.of(fearful).map(entry -> (Predicate<LivingEntity>) entity -> entity.getType().equals(entry)).toList());
    }

    @SafeVarargs
    public static FearedSkill<?> ofFearfulTag(TagKey<EntityType<?>>... fearful) {
        return new FearedSkill<>(Stream.of(fearful).map(entry -> (Predicate<LivingEntity>) entity -> entity.getType().is(entry)).toList());
    }

    @SafeVarargs
    public static FearedSkill<?> ofFearfulClass(Class<? extends LivingEntity>... fearful) {
        return new FearedSkill<>(Stream.of(fearful).map(entry -> (Predicate<LivingEntity>) entry::isInstance).toList());
    }

    public FearedSkill(List<Predicate<LivingEntity>> fearful) {
        this.fearful = fearful;
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
