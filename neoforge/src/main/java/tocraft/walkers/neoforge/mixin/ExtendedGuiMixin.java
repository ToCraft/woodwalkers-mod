package tocraft.walkers.neoforge.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.AttackForHealthSkill;
import tocraft.walkers.skills.impl.UndrownableSkill;

@OnlyIn(Dist.CLIENT)
@Mixin(value = ExtendedGui.class, remap = false)
public abstract class ExtendedGuiMixin {
    @Shadow
    public abstract Minecraft getMinecraft();

    @Inject(method = "renderAir", at = @At("HEAD"), cancellable = true)
    private void shouldRenderBreath(int width, int height, GuiGraphics guiGraphics, CallbackInfo ci) {
        Player player = this.getMinecraft().player;
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (player != null && shape != null) {
            if (player.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()) && (Walkers.isAquatic(shape) < 2 || SkillRegistry.has(shape, UndrownableSkill.ID))) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void shouldRenderFood(int width, int height, GuiGraphics guiGraphics, CallbackInfo ci) {
        Player player = this.getMinecraft().player;
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (shape != null) {
            if (SkillRegistry.has(shape, AttackForHealthSkill.ID)) {
                ci.cancel();
            }
        }
    }
}
