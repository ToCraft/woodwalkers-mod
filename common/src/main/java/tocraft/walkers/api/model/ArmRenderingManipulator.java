package tocraft.walkers.api.model;

import com.mojang.blaze3d.vertex.PoseStack;

public interface ArmRenderingManipulator<T> {
    void run(PoseStack stack, T model);
}
