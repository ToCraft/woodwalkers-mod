package tocraft.walkers.ability;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public abstract class GenericShapeAbility<E extends LivingEntity> extends ShapeAbility<E> {
    public abstract ResourceLocation getId();

    public abstract Codec<? extends GenericShapeAbility<?>> codec();
}
