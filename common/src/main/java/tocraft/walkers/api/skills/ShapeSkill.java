package tocraft.walkers.api.skills;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public abstract class ShapeSkill<E extends LivingEntity> {
    public abstract ResourceLocation getId();

    public abstract Codec<? extends ShapeSkill<?>> codec();
}
