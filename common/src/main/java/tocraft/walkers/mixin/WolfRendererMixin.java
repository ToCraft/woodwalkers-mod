package tocraft.walkers.mixin;

import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.Walkers;

@Mixin(WolfRenderer.class)
public class WolfRendererMixin {
    @Unique
    private static final ResourceLocation DEV_WILD = Walkers.id("textures/entity/wolf/dev_wild.png");
    @Unique
    private static final ResourceLocation DEV_TAMED = Walkers.id("textures/entity/wolf/dev_tame.png");
    @Unique
    private static final ResourceLocation DEV_ANGRY = Walkers.id("textures/entity/wolf/dev_angry.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Wolf;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(Wolf wolfEntity, CallbackInfoReturnable<ResourceLocation> ci) {
        CompoundTag nbt = new CompoundTag();
        wolfEntity.saveWithoutId(nbt);
        
        if (nbt.contains("isDev")) {
            if(nbt.getBoolean("isDev")) {
                if (wolfEntity.isTame()) {
                    ci.setReturnValue(DEV_TAMED);
                } else {
                    ci.setReturnValue(wolfEntity.isAngry() ? DEV_ANGRY : DEV_WILD);
                }
            }
        }
    }
}
