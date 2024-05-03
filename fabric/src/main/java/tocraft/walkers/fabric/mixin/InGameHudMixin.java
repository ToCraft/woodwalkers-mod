package tocraft.walkers.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.AttackForHealthSkill;
import tocraft.walkers.skills.impl.UndrownableSkill;

// FIXME: doesn't work on Forge/NeoForge
@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class InGameHudMixin {

    @Shadow
    protected abstract Player getCameraPlayer();

    @ModifyExpressionValue(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean shouldRenderBreath(boolean isEyeInFluid) {
        Player player = this.getCameraPlayer();
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (player != null && shape != null) {
            if (player.isEyeInFluid(FluidTags.WATER) && (Walkers.isAquatic(shape) < 2 || SkillRegistry.has(shape, UndrownableSkill.ID))) {
                return false;
            }
        }

        return isEyeInFluid;
    }

    @ModifyExpressionValue(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int shouldRenderHunger(int health) {
        Player player = this.getCameraPlayer();
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (shape != null) {
            if (SkillRegistry.has(shape, AttackForHealthSkill.ID)) {
                return -1; // return -1 so no hunger is displayed
            }
        }

        return health;
    }
}
