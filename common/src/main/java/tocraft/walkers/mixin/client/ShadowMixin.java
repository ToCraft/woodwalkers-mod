package tocraft.walkers.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.mixin.client.accessor.EntityShadowAccessor;

@Environment(EnvType.CLIENT)
@Mixin(value = EntityRenderDispatcher.class, priority = 999)
public abstract class ShadowMixin {

    @Unique
    private static Entity shape_shadowEntity;

    @Inject(
            method = "renderShadow",
            at = @At("HEAD"))
    private static void storeContext(PoseStack matrices, MultiBufferSource vertexConsumers, Entity entity, float opacity, float tickDelta, LevelReader world, float radius, CallbackInfo ci) {
        shape_shadowEntity = entity;
    }

    @ModifyVariable(
            method = "renderShadow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(DDD)D", ordinal = 0), index = 7)
    private static float adjustShadowSize(float originalSize) {
        if (shape_shadowEntity instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                EntityRenderer<?> r = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(shape);
                float shadowRadius = ((EntityShadowAccessor) r).getShadowRadius();
                float mod = shape.isBaby() ? .5f : 1;
                return shadowRadius * mod;
            }
        }

        return originalSize;
    }
}
