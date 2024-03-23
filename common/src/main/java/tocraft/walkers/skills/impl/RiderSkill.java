package tocraft.walkers.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class RiderSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("rider");
    public static final Codec<RiderSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).fieldOf("rideable").forGetter(o -> new ArrayList<>())
    ).apply(instance, instance.stable(RiderSkill::ofRideable)));

    public final List<Predicate<LivingEntity>> rideable;

    public static RiderSkill<?> ofRideable(List<ResourceLocation> hunter) {
        return new RiderSkill<>(hunter.stream().map(entry -> (Predicate<LivingEntity>) entity -> BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).equals(entry)).toList());
    }

    public static RiderSkill<?> ofRideableType(EntityType<?>... hunter) {
        return new RiderSkill<>(Stream.of(hunter).map(entry -> (Predicate<LivingEntity>) entity -> entity.getType().equals(entry)).toList());
    }

    @SafeVarargs
    public static RiderSkill<?> ofRideableClass(Class<? extends LivingEntity>... hunter) {
        return new RiderSkill<>(Stream.of(hunter).map(entry -> (Predicate<LivingEntity>) entry::isInstance).toList());
    }

    public RiderSkill(List<Predicate<LivingEntity>> rideable) {
        this.rideable = rideable;
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
