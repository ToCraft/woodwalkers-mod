package tocraft.walkers.api.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.skills.ShapeSkill;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PreySkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("prey");
    public static final Codec<PreySkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).fieldOf("hunter").forGetter(o -> null)
    ).apply(instance, instance.stable(PreySkill::ofHunter)));

    public final List<Predicate<LivingEntity>> hunter;

    public static PreySkill<?> ofHunter(List<ResourceLocation> hunter) {
        return new PreySkill<>(hunter.stream().map(entry -> (Predicate<LivingEntity>) entity -> BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).equals(entry)).toList());
    }

    public static PreySkill<?> ofHunterType(List<EntityType<?>> hunter) {
        return new PreySkill<>(hunter.stream().map(entry -> (Predicate<LivingEntity>) entity -> entity.getType().equals(entry)).toList());
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
