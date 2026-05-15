package dev.tocraft.walkers.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public interface ShapeRenderStateProvider {
    boolean walkers$shapeIsTameable();

    void walkers$setShapeIsTameable(boolean isTameable);

    @Nullable EntityRenderState walkers$getShapeRenderState();

    void walkers$setShapeRenderState(@NotNull Supplier<@Nullable EntityRenderState> shapeRenderState);

    @Nullable EntityRenderer<@NotNull LivingEntity, @NotNull EntityRenderState> walkers$getShapeRenderer();

    void walkers$setShapeRenderer(@NotNull Supplier<@Nullable EntityRenderer<@NotNull LivingEntity, @NotNull EntityRenderState>> shapeRenderer);

    void walkers$setInvisRide(boolean invisRide);

    boolean walkers$getInvisRide();
}
