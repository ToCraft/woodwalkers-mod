package dev.tocraft.walkers.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(value = EntityRenderDispatcher.class, priority = 999)
public abstract class ShadowMixin {
    // renderShadow was removed in 26.1.2; shadow size adjustment needs re-implementation
    // when the new shadow rendering API is understood.
}
