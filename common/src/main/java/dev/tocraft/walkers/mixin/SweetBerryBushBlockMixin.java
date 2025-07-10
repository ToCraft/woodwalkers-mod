package dev.tocraft.walkers.mixin;

import dev.tocraft.walkers.api.PlayerShape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
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

@Mixin(SweetBerryBushBlock.class)
public class SweetBerryBushBlockMixin {

    @Inject(
            method = "entityInside",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void onDamage(BlockState blockState, Level level, BlockPos blockPos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier, CallbackInfo ci) {
        if (entity instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            // Cancel damage if the player's shape is a fox
            if (shape instanceof Fox || shape instanceof Bee) {
                ci.cancel();
            }
        }
    }
}
