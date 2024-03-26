package tocraft.walkers.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.UndrownableSkill;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class InGameHudMixin {

    @Shadow
    protected abstract Player getCameraPlayer();

    @ModifyArg(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
    private TagKey<Fluid> shouldRenderBreath(TagKey<Fluid> tag) {
        Player player = this.getCameraPlayer();
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (shape != null) {
            if (Walkers.isAquatic(shape) < 2 || SkillRegistry.has(shape, UndrownableSkill.ID) && player.isEyeInFluid(FluidTags.WATER)) {
                return FluidTags.LAVA; // will cause isEyeInFluid to return false, preventing air render
            }
        }

        return tag;
    }
}
