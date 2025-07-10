package dev.tocraft.walkers.api.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface EntityArmProvider<T extends LivingEntity, R extends LivingEntityRenderState> {
    ModelPart getArm(T entity, EntityModel<R> model);
}
