package dev.tocraft.walkers.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tocraft.walkers.impl.ShapeRenderStateProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin(value = EntityRenderDispatcher.class, priority = 999)
public abstract class ShadowMixin {
    @ModifyArgs(method = "submit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitShadow(Lcom/mojang/blaze3d/vertex/PoseStack;FLjava/util/List;)V"))
    private void fixShadows(Args args, @Local EntityRenderState state) {
        if (state instanceof ShapeRenderStateProvider shapeStateProvider) {
            EntityRenderState shapeState = shapeStateProvider.walkers$getShapeRenderState();
            if (shapeState != null) {
                args.set(1, shapeState.shadowRadius);
                args.set(2, shapeState.shadowPieces);
            }
        }
    }
}
