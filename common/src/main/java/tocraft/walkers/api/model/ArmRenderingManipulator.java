package tocraft.walkers.api.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;

public interface ArmRenderingManipulator<T extends Model> {
    void run(PoseStack stack, T model);
}
