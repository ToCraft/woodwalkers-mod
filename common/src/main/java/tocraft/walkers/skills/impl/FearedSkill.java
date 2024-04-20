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

@SuppressWarnings("unused")
public class FearedSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("feared");
    public static final Codec<FearedSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("fearful", new ArrayList<>()).forGetter(o -> o.fearfulTypes.stream().map(BuiltInRegistries.ENTITY_TYPE::getKey).toList()),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("fearful_tags", new ArrayList<>()).forGetter(o -> o.fearfulTags.stream().map(TagKey::location).toList())
    ).apply(instance, instance.stable((preyLocations, preyTagLocations) -> {
        List<EntityType<?>> fearfulTypes = new ArrayList<>();
        List<TagKey<EntityType<?>>> fearfulTags = new ArrayList<>();
        for (ResourceLocation resourceLocation : preyLocations) {
            if (BuiltInRegistries.ENTITY_TYPE.containsKey(resourceLocation)) {
                fearfulTypes.add(BuiltInRegistries.ENTITY_TYPE.get(resourceLocation));
            }
        }
        for (ResourceLocation preyTagLocation : preyTagLocations) {
            fearfulTags.add(TagKey.create(Registries.ENTITY_TYPE, preyTagLocation));
        }
        return new FearedSkill<>(new ArrayList<>(), fearfulTypes, new ArrayList<>(), fearfulTags);
    })));

    private final List<Predicate<LivingEntity>> fearfulPredicates;
    private final List<EntityType<?>> fearfulTypes;
    private final List<Class<? extends LivingEntity>> fearfulClasses;
    private final List<TagKey<EntityType<?>>> fearfulTags;

    public static FearedSkill<?> ofFearfulType(EntityType<?>... fearful) {
        return new FearedSkill<>(new ArrayList<>(), List.of(fearful), new ArrayList<>(), new ArrayList<>());
    }

    @SafeVarargs
    public static FearedSkill<?> ofFearfulTag(TagKey<EntityType<?>>... fearful) {
        return new FearedSkill<>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), List.of(fearful));
    }

    @SafeVarargs
    public static FearedSkill<?> ofFearfulClass(Class<? extends LivingEntity>... fearful) {
        return new FearedSkill<>(new ArrayList<>(), new ArrayList<>(), List.of(fearful), new ArrayList<>());
    }

    public FearedSkill(List<Predicate<LivingEntity>> fearfulPredicates) {
        this(fearfulPredicates, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public FearedSkill(List<Predicate<LivingEntity>> fearfulPredicates, List<EntityType<?>> fearfulTypes, List<Class<? extends LivingEntity>> fearfulClasses, List<TagKey<EntityType<?>>> fearfulTags) {
        this.fearfulPredicates = fearfulPredicates;
        this.fearfulTypes = fearfulTypes;
        this.fearfulClasses = fearfulClasses;
        this.fearfulTags = fearfulTags;
    }

    public boolean isFeared(LivingEntity entity) {
        if (fearfulTypes.contains(entity.getType())) return true;
        if (fearfulClasses.contains(entity.getClass())) return true;
        for (TagKey<EntityType<?>> hunterTag : fearfulTags) {
            if (entity.getType().is(hunterTag)) return true;
        }
        for (Predicate<LivingEntity> hunterPredicate : fearfulPredicates) {
            if (hunterPredicate.test(entity)) return true;
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
