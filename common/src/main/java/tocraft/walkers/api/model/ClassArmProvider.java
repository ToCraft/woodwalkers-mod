package tocraft.walkers.api.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public interface ClassArmProvider<T> {
    ModelPart getArm(LivingEntity entity, T model);
}
