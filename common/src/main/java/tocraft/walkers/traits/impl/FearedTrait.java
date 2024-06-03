package tocraft.walkers.traits.impl;

import com.mojang.serialization.Codec;
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

@SuppressWarnings("unused")
public class FearedTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("feared");
    public static final Codec<FearedTrait<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
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
        return new FearedTrait<>(new ArrayList<>(), fearfulTypes, new ArrayList<>(), fearfulTags);
    })));

    private final List<Predicate<LivingEntity>> fearfulPredicates;
    private final List<EntityType<?>> fearfulTypes;
    private final List<Class<? extends LivingEntity>> fearfulClasses;
    private final List<TagKey<EntityType<?>>> fearfulTags;

    public static FearedTrait<?> ofFearfulType(EntityType<?>... fearful) {
        return new FearedTrait<>(new ArrayList<>(), List.of(fearful), new ArrayList<>(), new ArrayList<>());
    }

    @SafeVarargs
    public static FearedTrait<?> ofFearfulTag(TagKey<EntityType<?>>... fearful) {
        return new FearedTrait<>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), List.of(fearful));
    }

    @SafeVarargs
    public static FearedTrait<?> ofFearfulClass(Class<? extends LivingEntity>... fearful) {
        return new FearedTrait<>(new ArrayList<>(), new ArrayList<>(), List.of(fearful), new ArrayList<>());
    }

    public FearedTrait(@NotNull List<Predicate<LivingEntity>> fearfulPredicates) {
        this(fearfulPredicates, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public FearedTrait(@NotNull List<Predicate<LivingEntity>> fearfulPredicates, @NotNull List<EntityType<?>> fearfulTypes, @NotNull List<Class<? extends LivingEntity>> fearfulClasses, @NotNull List<TagKey<EntityType<?>>> fearfulTags) {
        this.fearfulPredicates = fearfulPredicates;
        this.fearfulTypes = fearfulTypes;
        this.fearfulClasses = fearfulClasses;
        this.fearfulTags = fearfulTags;
    }

    public boolean isFeared(LivingEntity entity) {
        if (fearfulTypes.contains(entity.getType())) return true;
        for (Class<? extends LivingEntity> fearfulClass : fearfulClasses) {
            if (fearfulClass.isInstance(entity)) return true;
        }
        for (TagKey<EntityType<?>> fearfulTag : fearfulTags) {
            if (entity.getType().is(fearfulTag)) return true;
        }
        for (Predicate<LivingEntity> fearfulPredicate : fearfulPredicates) {
            if (fearfulPredicate.test(entity)) return true;
        }
        return false;
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
