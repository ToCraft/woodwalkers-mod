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

@SuppressWarnings("unused")
public class PreySkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("prey");
    public static final Codec<PreySkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("hunter", new ArrayList<>()).forGetter(o -> o.hunterTypes.stream().map(Registry.ENTITY_TYPE::getKey).toList()),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("hunter_tags", new ArrayList<>()).forGetter(o -> o.hunterTags.stream().map(TagKey::location).toList())
    ).apply(instance, instance.stable((hunterLocations, hunterTagLocations) -> {
                List<EntityType<?>> hunterTypes = new ArrayList<>();
                List<TagKey<EntityType<?>>> hunterTags = new ArrayList<>();
                for (ResourceLocation resourceLocation : hunterLocations) {
                    if (Registry.ENTITY_TYPE.containsKey(resourceLocation)) {
                        hunterTypes.add(Registry.ENTITY_TYPE.get(resourceLocation));
                    }
                }
                for (ResourceLocation hunterTagLocation : hunterTagLocations) {
                    hunterTags.add(TagKey.create(Registry.ENTITY_TYPE_REGISTRY, hunterTagLocation));
                }
                return new PreySkill<>(new ArrayList<>(), hunterTypes, new ArrayList<>(), hunterTags);
            }
    )));

    private final List<Predicate<LivingEntity>> hunterPredicates;
    private final List<EntityType<?>> hunterTypes;
    private final List<Class<? extends LivingEntity>> hunterClasses;
    private final List<TagKey<EntityType<?>>> hunterTags;

    public static PreySkill<?> ofHunterType(EntityType<?>... hunter) {
        return new PreySkill<>(new ArrayList<>(), List.of(hunter), new ArrayList<>(), new ArrayList<>());
    }

    @SafeVarargs
    public static PreySkill<?> ofHunterTag(TagKey<EntityType<?>>... hunter) {
        return new PreySkill<>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), List.of(hunter));
    }

    @SafeVarargs
    public static PreySkill<?> ofHunterClass(Class<? extends LivingEntity>... hunter) {
        return new PreySkill<>(new ArrayList<>(), new ArrayList<>(), List.of(hunter), new ArrayList<>());
    }


    public PreySkill(@NotNull List<Predicate<LivingEntity>> hunterPredicates) {
        this(hunterPredicates, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public PreySkill(@NotNull List<Predicate<LivingEntity>> hunterPredicates, @NotNull List<EntityType<?>> hunterTypes, @NotNull List<Class<? extends LivingEntity>> hunterClasses, @NotNull List<TagKey<EntityType<?>>> hunterTags) {
        this.hunterPredicates = hunterPredicates;
        this.hunterTypes = hunterTypes;
        this.hunterClasses = hunterClasses;
        this.hunterTags = hunterTags;
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

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends ShapeSkill<?>> codec() {
        return CODEC;
    }
}
