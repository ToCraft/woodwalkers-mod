package tocraft.walkers.api.skills.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.skills.ShapeSkill;

public class NoPhysicsSkill<E extends LivingEntity> extends ShapeSkill<E> {
    public static final ResourceLocation ID = Walkers.id("no_physics");
    public static final Codec<NoPhysicsSkill<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.stable(new NoPhysicsSkill<>()));

    public NoPhysicsSkill() {
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
