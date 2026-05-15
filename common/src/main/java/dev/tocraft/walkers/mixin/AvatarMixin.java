package dev.tocraft.walkers.mixin;

import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.traits.TraitRegistry;
import dev.tocraft.walkers.traits.impl.HumanoidTrait;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Avatar.class)
public class AvatarMixin {
    @SuppressWarnings("ConstantValue")
    @Inject(method = "getDefaultDimensions", at = @At("HEAD"), cancellable = true, require = 0)
    private void getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if ((Object) this instanceof Player player) {
            LivingEntity entity = PlayerShape.getCurrentShape(player);
            if (entity != null) {
                if (pose != Pose.CROUCHING || !TraitRegistry.has(entity, HumanoidTrait.ID)) {
                    cir.setReturnValue(entity.getDimensions(pose));
                }
            }
        }
    }
}
