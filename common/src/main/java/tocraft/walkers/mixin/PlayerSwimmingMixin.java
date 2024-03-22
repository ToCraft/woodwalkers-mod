package tocraft.walkers.mixin;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.CantSwimSkill;

@Mixin(LivingEntity.class)
public class PlayerSwimmingMixin {

    @Inject(
            method = "jumpInLiquid", at = @At("HEAD"), cancellable = true)
    private void onGolemSwimUp(TagKey<Fluid> fluid, CallbackInfo ci) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        if (thisEntity instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null && SkillRegistry.has(shape, CantSwimSkill.ID)) {
                ci.cancel();
            }
        }
    }
}
