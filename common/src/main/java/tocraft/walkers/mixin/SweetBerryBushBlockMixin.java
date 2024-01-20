package tocraft.walkers.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerShape;

@Mixin(SweetBerryBushBlock.class)
public class SweetBerryBushBlockMixin {

    @Inject(
            method = "entityInside",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void onDamage(BlockState state, Level world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            // Cancel damage if the player's shape is a fox
            if (shape instanceof Fox || shape instanceof Bee) {
                ci.cancel();
            }
        }
    }
}
