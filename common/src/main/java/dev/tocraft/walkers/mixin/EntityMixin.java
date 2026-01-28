package dev.tocraft.walkers.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.traits.TraitRegistry;
import dev.tocraft.walkers.traits.impl.NoPhysicsTrait;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantConditions")
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
    private void fireImmune(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                cir.setReturnValue(shape.getType().fireImmune());
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void goThroughBlocks(CallbackInfo ci) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape != null) {
                if (TraitRegistry.has(shape, NoPhysicsTrait.ID)) {
                    player.noPhysics = true;
                }
            }
        }
    }

    @Inject(method = "removePassenger", at = @At("TAIL"))
    private void removePlayerVehicle(Entity passenger, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayer vehicle && !vehicle.level().isClientSide) {
            vehicle.connection.send(new ClientboundSetPassengersPacket(vehicle));
        }
    }

    @Inject(method = "addPassenger", at = @At("TAIL"))
    private void addPlayerVehicle(Entity passenger, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayer vehicle && !vehicle.level().isClientSide) {
            vehicle.connection.send(new ClientboundSetPassengersPacket(vehicle));
        }
    }

    @Inject(method = "unRide", at = @At("HEAD"))
    private void onUnRide(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity.level().isClientSide())
            return;

        Entity vehicle = entity.getVehicle();
        if (vehicle instanceof Player) {
            entity.stopRiding();
        }
    }

    @SuppressWarnings("rawtypes")
    @WrapOperation(
            method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z")
    )
    private boolean allowRidingPlayers(EntityType instance, Operation<Boolean> original) {
        if (instance == EntityType.PLAYER) {
            return true;
        } else {
            return original.call(instance);
        }
    }
}
