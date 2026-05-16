package dev.tocraft.walkers.mixin.client;

import dev.tocraft.craftedcore.util.MemoizingSupplier;
import dev.tocraft.walkers.impl.ShapeRenderStateProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
@Mixin(AvatarRenderState.class)
public class PlayerRenderStateMixin implements ShapeRenderStateProvider {
    @Unique
    private boolean walkers$shapeIsTameable = false;
    @Unique
    private @NotNull Supplier<EntityRenderState> walkers$shapeRenderState = () -> null;
    @Unique
    private @NotNull Supplier<EntityRenderer<@NotNull LivingEntity, @NotNull EntityRenderState>> walkers$shapeRenderer = () -> null;
    @Unique
    private boolean walkers$invisRide = false;

    @Override
    public boolean walkers$shapeIsTameable() {
        return walkers$shapeIsTameable;
    }

    @Override
    public void walkers$setShapeIsTameable(boolean isTameable) {
        this.walkers$shapeIsTameable = isTameable;
    }

    @Override
    public @Nullable EntityRenderState walkers$getShapeRenderState() {
        return walkers$shapeRenderState.get();
    }

    @Override
    public void walkers$setShapeRenderState(@NotNull Supplier<@Nullable EntityRenderState> shapeRenderState) {
        this.walkers$shapeRenderState = new MemoizingSupplier<>(shapeRenderState);
    }

    @Unique
    @Override
    public @Nullable EntityRenderer<@NotNull LivingEntity, @NotNull EntityRenderState> walkers$getShapeRenderer() {
        return walkers$shapeRenderer.get();
    }

    @Unique
    @Override
    public void walkers$setShapeRenderer(@NotNull Supplier<@Nullable EntityRenderer<@NotNull LivingEntity, @NotNull EntityRenderState>> shapeRenderer) {
        walkers$shapeRenderer = new MemoizingSupplier<>(shapeRenderer);
    }

    @Override
    public void walkers$setInvisRide(boolean invisRide) {
        this.walkers$invisRide = invisRide;
    }

    @Override
    public boolean walkers$getInvisRide() {
        return this.walkers$invisRide;
    }
}
