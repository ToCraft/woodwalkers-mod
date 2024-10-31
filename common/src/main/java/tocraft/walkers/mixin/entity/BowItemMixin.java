package tocraft.walkers.mixin.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerShape;

@Mixin(BowItem.class)
public class BowItemMixin {
    @Inject(method = "shootProjectile", at = @At("HEAD"))
    private void flameArrows(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, LivingEntity target, CallbackInfo ci) {
        if (shooter instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape instanceof WitherSkeleton) {
                projectile.igniteForSeconds(100);
            }
        }
    }
}
