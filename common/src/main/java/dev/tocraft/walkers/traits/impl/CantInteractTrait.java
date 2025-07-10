package dev.tocraft.walkers.traits.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.traits.ShapeTrait;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CantInteractTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("cant_interact");
    public static final MapCodec<CantInteractTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("can_only_interact_with_listed", false).forGetter(o -> o.canOnlyInteractWithListed),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("types", new ArrayList<>()).forGetter(o -> o.types.stream().map(EntityType::getKey).toList()),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("tags", new ArrayList<>()).forGetter(o -> o.tags.stream().map(TagKey::location).toList())
    ).apply(instance, instance.stable((canOnlyInteractWithListed, typeIds, tagIds) -> {
        List<EntityType<?>> reinforcements = new ArrayList<>();
        List<TagKey<EntityType<?>>> reinforcementTags = new ArrayList<>();
        for (ResourceLocation resourceLocation : typeIds) {
            if (BuiltInRegistries.ENTITY_TYPE.containsKey(resourceLocation)) {
                reinforcements.add(BuiltInRegistries.ENTITY_TYPE.get(resourceLocation).orElseThrow().value());
            }
        }
        for (ResourceLocation resourceLocation : tagIds) {
            reinforcementTags.add(TagKey.create(Registries.ENTITY_TYPE, resourceLocation));
        }
        return new CantInteractTrait<>(canOnlyInteractWithListed, reinforcements, reinforcementTags);
    })));
    private final boolean canOnlyInteractWithListed;
    private final List<Class<? extends Entity>> classes;
    private final List<EntityType<?>> types;
    private final List<TagKey<EntityType<?>>> tags;

    public CantInteractTrait(@NotNull List<EntityType<?>> types, @NotNull List<TagKey<EntityType<?>>> tags) {
        this(false, new ArrayList<>(), types, tags);
    }

    public CantInteractTrait(boolean canOnlyInteractWithListed, @NotNull List<EntityType<?>> types, @NotNull List<TagKey<EntityType<?>>> tags) {
        this(canOnlyInteractWithListed, new ArrayList<>(), types, tags);
    }

    public CantInteractTrait(@NotNull List<Class<? extends Entity>> classes) {
        this(false, classes, new ArrayList<>(), new ArrayList<>());
    }

    public CantInteractTrait(boolean canOnlyInteractWithListed, @NotNull List<Class<? extends Entity>> classes) {
        this(canOnlyInteractWithListed, classes, new ArrayList<>(), new ArrayList<>());
    }

    public CantInteractTrait(@NotNull List<Class<? extends Entity>> classes, @NotNull List<EntityType<?>> types, @NotNull List<TagKey<EntityType<?>>> tags) {
        this(false, classes, types, tags);
    }

    public CantInteractTrait(boolean canOnlyInteractWithListed, @NotNull List<Class<? extends Entity>> classes, @NotNull List<EntityType<?>> types, @NotNull List<TagKey<EntityType<?>>> tags) {
        this.canOnlyInteractWithListed = canOnlyInteractWithListed;
        this.classes = classes;
        this.types = types;
        this.tags = tags;
    }

    public boolean canInteractWithEntity(Entity entity) {
        boolean bool = false;
        for (Class<? extends Entity> clazz : classes) {
            if (clazz.isInstance(entity)) {
                bool = true;
                break;
            }
        }

        if (types.contains(entity.getType())) {
            bool = true;
        }

        for (TagKey<EntityType<?>> tag : tags) {
            if (entity.getType().is(tag)) {
                bool = true;
                break;
            }
        }

        return bool == this.canOnlyInteractWithListed;
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
