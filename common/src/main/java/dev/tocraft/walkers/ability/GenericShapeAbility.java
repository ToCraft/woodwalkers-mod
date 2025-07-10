package dev.tocraft.walkers.ability;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.LivingEntity;

public abstract class GenericShapeAbility<E extends LivingEntity> extends ShapeAbility<E> {
    public abstract MapCodec<? extends GenericShapeAbility<?>> codec();
}
