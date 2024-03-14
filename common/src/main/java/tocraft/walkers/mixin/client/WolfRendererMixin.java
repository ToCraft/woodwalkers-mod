package tocraft.walkers.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

@Environment(EnvType.CLIENT)
@Mixin(WolfRenderer.class)
public class WolfRendererMixin {
    @Unique
    private static final ResourceLocation SPECIAL_WILD = Walkers.id("textures/entity/wolf/special_wild.png");
    @Unique
    private static final ResourceLocation SPECIAL_TAMED = Walkers.id("textures/entity/wolf/special_tame.png");
    @Unique
    private static final ResourceLocation SPECIAL_ANGRY = Walkers.id("textures/entity/wolf/special_angry.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Wolf;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(Wolf wolfEntity, CallbackInfoReturnable<ResourceLocation> ci) {
        CompoundTag nbt = new CompoundTag();
        wolfEntity.saveWithoutId(nbt);

        if (nbt.contains("isSpecial")) {
            if (nbt.getBoolean("isSpecial")) {
                if (wolfEntity.isTame()) {
                    ci.setReturnValue(SPECIAL_TAMED);
                } else {
                    ci.setReturnValue(wolfEntity.isAngry() ? SPECIAL_ANGRY : SPECIAL_WILD);
                }
            }
        }
    }
}
