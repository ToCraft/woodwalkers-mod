package tocraft.walkers.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;

@Mixin(MouseHandler.class)
@Environment(EnvType.CLIENT)
public class MouseHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapWithCondition(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;swapPaint(D)V"))
    private boolean handleScrollInVariantsMenu(Inventory instance, double direction) {
        if (!minecraft.options.hideGui && WalkersClient.isRenderingVariantsMenu && Walkers.CONFIG.unlockEveryVariant && minecraft.screen == null) {
            WalkersClient.variantOffset -= (int) direction;
            return false;
        } else {
            return true;
        }
    }
}
