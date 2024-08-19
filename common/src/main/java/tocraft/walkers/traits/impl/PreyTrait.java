package tocraft.walkers.traits.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
public class PreyTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("prey");
    public static final MapCodec<PreyTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("hunter", new ArrayList<>()).forGetter(o -> o.hunterTypes.stream().map(Walkers.getEntityTypeRegistry()::getKey).toList()),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("hunter_tags", new ArrayList<>()).forGetter(o -> o.hunterTags.stream().map(TagKey::location).toList())
    ).apply(instance, instance.stable((hunterLocations, hunterTagLocations) -> {
                List<EntityType<?>> hunterTypes = new ArrayList<>();
                List<TagKey<EntityType<?>>> hunterTags = new ArrayList<>();
                for (ResourceLocation resourceLocation : hunterLocations) {
                    if (Walkers.getEntityTypeRegistry().containsKey(resourceLocation)) {
                        hunterTypes.add(Walkers.getEntityTypeRegistry().get(resourceLocation));
                    }
                }
                for (ResourceLocation hunterTagLocation : hunterTagLocations) {
                    hunterTags.add(TagKey.create(Walkers.getEntityTypeRegistry().key(), hunterTagLocation));
                }
                return new PreyTrait<>(new ArrayList<>(), hunterTypes, new ArrayList<>(), hunterTags);
            }
    )));

    private final List<Predicate<LivingEntity>> hunterPredicates;
    private final List<EntityType<?>> hunterTypes;
    private final List<Class<? extends LivingEntity>> hunterClasses;
    private final List<TagKey<EntityType<?>>> hunterTags;
    private final int priority;
    private final int randInt;

    public static PreyTrait<?> ofHunterType(EntityType<?>... hunter) {
        return new PreyTrait<>(new ArrayList<>(), List.of(hunter), new ArrayList<>(), new ArrayList<>());
    }

    @SafeVarargs
    public static PreyTrait<?> ofHunterTag(TagKey<EntityType<?>>... hunter) {
        return new PreyTrait<>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), List.of(hunter));
    }

    @SafeVarargs
    public static PreyTrait<?> ofHunterClass(Class<? extends LivingEntity>... hunter) {
        return new PreyTrait<>(new ArrayList<>(), new ArrayList<>(), List.of(hunter), new ArrayList<>());
    }


    public PreyTrait(@NotNull List<Predicate<LivingEntity>> hunterPredicates) {
        this(hunterPredicates, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public PreyTrait(@NotNull List<Predicate<LivingEntity>> hunterPredicates, @NotNull List<EntityType<?>> hunterTypes, @NotNull List<Class<? extends LivingEntity>> hunterClasses, @NotNull List<TagKey<EntityType<?>>> hunterTags) {
        this(hunterPredicates, hunterTypes, hunterClasses, hunterTags, 7, 10);
    }

    public PreyTrait(@NotNull List<Predicate<LivingEntity>> hunterPredicates, @NotNull List<EntityType<?>> hunterTypes, @NotNull List<Class<? extends LivingEntity>> hunterClasses, @NotNull List<TagKey<EntityType<?>>> hunterTags, int priority, int randInt) {
        this.hunterPredicates = hunterPredicates;
        this.hunterTypes = hunterTypes;
        this.hunterClasses = hunterClasses;
        this.hunterTags = hunterTags;
        this.priority = priority;
        this.randInt = randInt;
    }

    public boolean isHunter(LivingEntity entity) {
        if (hunterTypes.contains(entity.getType())) return true;
        for (Class<? extends LivingEntity> hunterClass : hunterClasses) {
            if (hunterClass.isInstance(entity)) return true;
        }
        for (TagKey<EntityType<?>> hunterTag : hunterTags) {
            if (entity.getType().is(hunterTag)) return true;
        }
        for (Predicate<LivingEntity> hunterPredicate : hunterPredicates) {
            if (hunterPredicate.test(entity)) return true;
        }
        return false;
    }

    public int getPriority() {
        return priority;
    }

    public int getRandInt() {
        return randInt;
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
