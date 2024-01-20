package tocraft.walkers.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.api.PlayerShape;

@Mixin(Villager.class)
public abstract class VillagerEntityMixin {

    @Shadow
    protected abstract void stopTrading();

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);

        if (shape != null && shape.isInvertedHealAndHarm()) {
            this.stopTrading();
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
