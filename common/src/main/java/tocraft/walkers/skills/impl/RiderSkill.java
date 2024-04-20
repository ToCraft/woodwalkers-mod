package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class RiderSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("rider");
    public static final Codec<RiderSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("rideable", new ArrayList<>()).forGetter(o -> o.rideableTypes.stream().map(Registry.ENTITY_TYPE::getKey).toList()),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("rideable_tags", new ArrayList<>()).forGetter(o -> o.rideableTags.stream().map(TagKey::location).toList())
    ).apply(instance, instance.stable((rideableTypeIds, rideableTagIds) -> {
        List<EntityType<?>> rideableTypes = new ArrayList<>();
        List<TagKey<EntityType<?>>> rideableTags = new ArrayList<>();
        for (ResourceLocation rideableTypeId : rideableTypeIds) {
            if (Registry.ENTITY_TYPE.containsKey(rideableTypeId)) {
                rideableTypes.add(Registry.ENTITY_TYPE.get(rideableTypeId));
            }
        }
        for (ResourceLocation rideableTagId : rideableTagIds) {
            rideableTags.add(TagKey.create(Registry.ENTITY_TYPE_REGISTRY, rideableTagId));
        }
        return new RiderSkill<>(new ArrayList<>(), rideableTypes, new ArrayList<>(), rideableTags);
    })));

    private final List<Predicate<LivingEntity>> rideablePredicates;
    private final List<EntityType<?>> rideableTypes;
    private final List<Class<? extends LivingEntity>> rideableClasses;
    private final List<TagKey<EntityType<?>>> rideableTags;

    public static RiderSkill<?> ofRideableType(EntityType<?>... rideable) {
        return new RiderSkill<>(Stream.of(rideable).map(entry -> (Predicate<LivingEntity>) entity -> entity.getType().equals(entry)).toList());
    }

    @SafeVarargs
    public static RiderSkill<?> ofRideableClass(Class<? extends LivingEntity>... rideable) {
        return new RiderSkill<>(Stream.of(rideable).map(entry -> (Predicate<LivingEntity>) entry::isInstance).toList());
    }

    public RiderSkill(@NotNull List<Predicate<LivingEntity>> rideablePredicates) {
        this(rideablePredicates, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public RiderSkill(@NotNull List<Predicate<LivingEntity>> rideablePredicates, @NotNull List<EntityType<?>> rideableTypes, @NotNull List<Class<? extends LivingEntity>> rideableClasses, @NotNull List<TagKey<EntityType<?>>> rideableTags) {
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
    public Codec<? extends ShapeSkill<?>> codec() {
        return CODEC;
    }
}
