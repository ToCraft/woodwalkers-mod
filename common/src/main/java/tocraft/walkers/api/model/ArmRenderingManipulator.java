package tocraft.walkers.api.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import tocraft.craftedcore.math.math;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ArmRenderingManipulator<T extends Model> {
    void run(PoseStack stack, T model);
}
