package tocraft.walkers.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.impl.ShapeRenderStateProvider;
import tocraft.walkers.mixin.client.accessor.EntityShadowAccessor;

@Environment(EnvType.CLIENT)
@Mixin(value = EntityRenderDispatcher.class, priority = 999)
public abstract class ShadowMixin {
    @Unique
    private static EntityRenderState shape_shadowState;

    @Inject(
            method = "renderShadow",
            at = @At("HEAD"))
    private static void storeContext(PoseStack poseStack, MultiBufferSource multiBufferSource, EntityRenderState state, float f, LevelReader levelReader, float g, CallbackInfo ci) {
        shape_shadowState = state;
    }

    /*
    @ModifyVariable(
            method = "renderShadow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I", ordinal = 0), index = 6, argsOnly = true)
    private static float adjustShadowSize(float value) {
        if (shape_shadowState instanceof PlayerRenderState playerState) {
            LivingEntity shape = ((ShapeRenderStateProvider) playerState).walkers$getShape();

            if (shape != null) {
                EntityRenderer<?, ?> r = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(shape);
                float shadowRadius = ((EntityShadowAccessor) r).getShadowRadius();
                float mod = shape.isBaby() ? .5f : 1;
                return shadowRadius * mod;
            }
        }

        return value;
    }*/
}
