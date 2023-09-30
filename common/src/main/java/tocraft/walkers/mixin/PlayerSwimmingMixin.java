package tocraft.walkers.mixin;

import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.registry.WalkersEntityTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class PlayerSwimmingMixin {

    @Inject(
            method = "jumpInLiquid", at = @At("HEAD"), cancellable = true)
    private void onGolemSwimUp(TagKey<Fluid> fluid, CallbackInfo ci) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        if(thisEntity instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if(shape != null && shape.getType().is(WalkersEntityTags.CANT_SWIM)) {
                ci.cancel();
            }
        }
    }
}
