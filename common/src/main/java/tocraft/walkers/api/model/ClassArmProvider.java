package tocraft.walkers.api.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ClassArmProvider<T extends Model> {
    ModelPart getArm(LivingEntity entity, T model);
}
