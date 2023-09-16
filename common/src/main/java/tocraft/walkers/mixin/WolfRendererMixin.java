package tocraft.walkers.mixin;

import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import tocraft.walkers.Walkers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntityRenderer.class)
public class WolfRendererMixin {
    private static final Identifier DEV_WILD = Walkers.id("textures/entity/wolf/dev_wild.png");
    private static final Identifier DEV_TAMED = Walkers.id("textures/entity/wolf/dev_tame.png");
    private static final Identifier DEV_ANGRY = Walkers.id("textures/entity/wolf/dev_angry.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(WolfEntity wolfEntity, CallbackInfoReturnable<Identifier> ci) {
        NbtCompound nbt = new NbtCompound();
        wolfEntity.writeNbt(nbt);
        
        if (nbt.contains("isDev")) {
            if(nbt.getBoolean("isDev")) {
                if (wolfEntity.isTamed()) {
                    ci.setReturnValue(DEV_TAMED);
                } else {
                    ci.setReturnValue(wolfEntity.hasAngerTime() ? DEV_ANGRY : DEV_WILD);
                }
            }
        }
    }
}
