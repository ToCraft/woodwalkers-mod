package tocraft.walkers.mixin.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tocraft.walkers.api.PlayerShape;

@Mixin(BowItem.class)
public class BowItemMixin {
    //#if MC>=1205
    @Inject(method = "shootProjectile", at = @At("HEAD"))
    private void flameArrows(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, LivingEntity target, CallbackInfo ci) {
        if (shooter instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape instanceof WitherSkeleton) {
                projectile.igniteForSeconds(100);
            }
        }
    }
    //#else
    //$$ @Inject(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    //$$ private void flameArrows(ItemStack stack, Level level, LivingEntity shooter, int timeCharged, CallbackInfo ci, Player playerEntity, boolean bl, ItemStack itemStack, int i, float f, boolean bl2, ArrowItem arrowItem, AbstractArrow projectile) {
    //$$     if (shooter instanceof Player player) {
    //$$         LivingEntity shape = PlayerShape.getCurrentShape(player);
    //$$         if (shape instanceof WitherSkeleton) {
    //$$             projectile.setSecondsOnFire(100);
    //$$         }
    //$$     }
    //$$ }
    //#endif
}
