package tocraft.walkers.traits.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.Walkers;
import tocraft.walkers.traits.ShapeTrait;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class RiderTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("rider");
    public static final MapCodec<RiderTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("rideable", new ArrayList<>()).forGetter(o -> o.rideableTypes.stream().map(BuiltInRegistries.ENTITY_TYPE::getKey).toList()),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("rideable_tags", new ArrayList<>()).forGetter(o -> o.rideableTags.stream().map(TagKey::location).toList())
    ).apply(instance, instance.stable((rideableTypeIds, rideableTagIds) -> {
        List<EntityType<?>> rideableTypes = new ArrayList<>();
        List<TagKey<EntityType<?>>> rideableTags = new ArrayList<>();
        for (ResourceLocation rideableTypeId : rideableTypeIds) {
            if (BuiltInRegistries.ENTITY_TYPE.containsKey(rideableTypeId)) {
                rideableTypes.add(BuiltInRegistries.ENTITY_TYPE.get(rideableTypeId));
            }
        }
        for (ResourceLocation rideableTagId : rideableTagIds) {
            rideableTags.add(TagKey.create(Registries.ENTITY_TYPE, rideableTagId));
        }
        return new RiderTrait<>(new ArrayList<>(), rideableTypes, new ArrayList<>(), rideableTags);
    })));

    private final List<Predicate<LivingEntity>> rideablePredicates;
    private final List<EntityType<?>> rideableTypes;
    private final List<Class<? extends LivingEntity>> rideableClasses;
    private final List<TagKey<EntityType<?>>> rideableTags;

    public static RiderTrait<?> ofRideableType(EntityType<?>... rideable) {
        return new RiderTrait<>(Stream.of(rideable).map(entry -> (Predicate<LivingEntity>) entity -> entity.getType().equals(entry)).toList());
    }

    @SafeVarargs
    public static RiderTrait<?> ofRideableClass(Class<? extends LivingEntity>... rideable) {
        return new RiderTrait<>(Stream.of(rideable).map(entry -> (Predicate<LivingEntity>) entry::isInstance).toList());
    }

    public RiderTrait(@NotNull List<Predicate<LivingEntity>> rideablePredicates) {
        this(rideablePredicates, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public RiderTrait(@NotNull List<Predicate<LivingEntity>> rideablePredicates, @NotNull List<EntityType<?>> rideableTypes, @NotNull List<Class<? extends LivingEntity>> rideableClasses, @NotNull List<TagKey<EntityType<?>>> rideableTags) {
        this.rideablePredicates = rideablePredicates;
        this.rideableTypes = rideableTypes;
        this.rideableClasses = rideableClasses;
        this.rideableTags = rideableTags;
    }

    public boolean isRideable(LivingEntity entity) {
        if (rideableTypes.contains(entity.getType())) return true;
        for (Class<? extends LivingEntity> rideableClass : rideableClasses) {
            if (rideableClass.isInstance(entity)) return true;
        }
        for (TagKey<EntityType<?>> rideableTag : rideableTags) {
            if (entity.getType().is(rideableTag)) return true;
        }
        for (Predicate<LivingEntity> rideablePredicate : rideablePredicates) {
            if (rideablePredicate.test(entity)) return true;
        }
        return false;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }
}
