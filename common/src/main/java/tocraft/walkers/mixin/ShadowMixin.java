package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.mixin.accessor.EntityShadowAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class ShadowMixin {

    @Unique
    private static Entity shape_shadowEntity;

    @Inject(
            method = "renderShadow",
            at = @At("HEAD"))
    private static void storeContext(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius, CallbackInfo ci) {
        shape_shadowEntity = entity;
    }

    @ModifyVariable(
            method = "renderShadow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(DDD)D", ordinal = 0), index = 7)
    private static float adjustShadowSize(float originalSize) {
        if(shape_shadowEntity instanceof PlayerEntity player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if(shape != null) {
                EntityRenderer<?> r = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(shape);
                float shadowRadius = ((EntityShadowAccessor) r).getShadowRadius();
                float mod = shape.isBaby() ? .5f : 1;
                return shadowRadius * mod;
            }
        }

        return originalSize;
    }
}
