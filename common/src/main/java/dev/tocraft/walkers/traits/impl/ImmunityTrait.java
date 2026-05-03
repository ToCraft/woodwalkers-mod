package dev.tocraft.walkers.traits.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.traits.ShapeTrait;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public class ImmunityTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final Identifier ID = Walkers.id("immunity");
    public static final MapCodec<ImmunityTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Identifier.CODEC.fieldOf("effect").forGetter(o -> BuiltInRegistries.MOB_EFFECT.getKey(o.effect))
    ).apply(instance, instance.stable((effect) -> new ImmunityTrait<>(BuiltInRegistries.MOB_EFFECT.get(effect).orElseThrow().value()))));

    public final MobEffect effect;

    public ImmunityTrait(MobEffect effect) {
        this.effect = effect;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }
}
