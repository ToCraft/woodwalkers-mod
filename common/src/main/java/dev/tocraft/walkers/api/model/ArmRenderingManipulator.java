package dev.tocraft.walkers.api.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ArmRenderingManipulator<T extends Model> {
    void run(PoseStack stack, T model);
}
