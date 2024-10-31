package tocraft.walkers.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public interface ShapeRenderStateProvider {
    @Nullable LivingEntity walkers$getShape();

    void walkers$setShape(@NotNull Supplier<@Nullable LivingEntity> shape);
}
