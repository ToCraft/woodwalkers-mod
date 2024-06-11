package tocraft.walkers.ability;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public abstract class GenericShapeAbility<E extends LivingEntity> extends ShapeAbility<E> {
    public abstract ResourceLocation getId();

    public abstract MapCodec<? extends GenericShapeAbility<?>> codec();
}
