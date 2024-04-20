package tocraft.walkers.mixin.player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerShape;

@Mixin(Player.class)
public abstract class PlayerEntityAttackMixin extends LivingEntity {

    private PlayerEntityAttackMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;skipAttackInteraction(Lnet/minecraft/world/entity/Entity;)Z"), cancellable = true)
    private void shapeAttack(Entity target, CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape((Player) (Object) this);

        if (shape != null) {
            if (getMainHandItem().isEmpty()) {
                try {
                    shape.doHurtTarget(target);
                    ci.cancel();
                } catch (Exception ignored) {
                    // FALL BACK TO DEFAULT BEHAVIOR.
                    // Some mobs do not override, so it defaults to attack damage attribute, but the identity does not have any
                }
            }
        }
    }
}
