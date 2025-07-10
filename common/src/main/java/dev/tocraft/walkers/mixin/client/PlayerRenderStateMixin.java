package dev.tocraft.walkers.mixin.client;

import dev.tocraft.walkers.impl.ShapeRenderStateProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
@Mixin(PlayerRenderState.class)
public class PlayerRenderStateMixin implements ShapeRenderStateProvider {
    @Unique
    private @NotNull Supplier<@Nullable LivingEntity> walkers$shape = () -> null;

    @Unique
    @Override
    public @Nullable LivingEntity walkers$getShape() {
        return walkers$shape.get();
    }

    @Unique
    @Override
    public void walkers$setShape(@NotNull Supplier<@Nullable LivingEntity> shape) {
        walkers$shape = shape;
    }
}
