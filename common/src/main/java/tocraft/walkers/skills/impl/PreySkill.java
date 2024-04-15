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

public class PreySkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("prey");
    public static final Codec<PreySkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).fieldOf("hunter").forGetter(o -> new ArrayList<>())
    ).apply(instance, instance.stable(hunterLocations -> {
                List<Predicate<LivingEntity>> hunter = new ArrayList<>();
                for (ResourceLocation resourceLocation : hunterLocations) {
                    if (BuiltInRegistries.ENTITY_TYPE.containsKey(resourceLocation)) {
                        hunter.add(entity -> entity.getType().equals(BuiltInRegistries.ENTITY_TYPE.get(resourceLocation)));
                    } else {
                        hunter.add(entity -> entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, resourceLocation)));
                    }
                }
                return new PreySkill<>(hunter);
            }
    )));

    public final List<Predicate<LivingEntity>> hunter;

    public static PreySkill<?> ofHunterType(EntityType<?>... hunter) {
        return new PreySkill<>(Stream.of(hunter).map(entry -> (Predicate<LivingEntity>) entity -> entity.getType().equals(entry)).toList());
    }

    @SafeVarargs
    public static PreySkill<?> ofHunterTag(TagKey<EntityType<?>>... hunter) {
        return new PreySkill<>(Stream.of(hunter).map(entry -> (Predicate<LivingEntity>) entity -> entity.getType().is(entry)).toList());
    }

    @SafeVarargs
    public static PreySkill<?> ofHunterClass(Class<? extends LivingEntity>... hunter) {
        return new PreySkill<>(Stream.of(hunter).map(entry -> (Predicate<LivingEntity>) entry::isInstance).toList());
    }

    public PreySkill(List<Predicate<LivingEntity>> hunter) {
        this.hunter = hunter;
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
