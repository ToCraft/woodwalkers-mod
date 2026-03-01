package dev.tocraft.walkers.mixin;

import dev.tocraft.walkers.impl.PlayerDataProvider;
import dev.tocraft.walkers.impl.ShapeDataProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpellcasterIllager.class)
public class EvokerMixin {
    @Inject(method = "isCastingSpell", at = @At("HEAD"), cancellable = true)
    public void castAnimation(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof ShapeDataProvider shapeData) {
            Entity player = entity.level().getEntity(shapeData.walkers$shapedPlayer());
            if (player instanceof PlayerDataProvider playerData) {
                cir.setReturnValue(playerData.walkers$getIsSpecialAnim());
            }
        }
    }
}
