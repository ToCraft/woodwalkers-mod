package tocraft.walkers.mixin;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;

import java.util.*;

@Mixin(value = LivingEntity.class)
public abstract class DisableSlotsMixin {
    @Inject(method = "getItemBySlot", at = @At("HEAD"), cancellable = true)
    private void onGetAmorItem(@NotNull EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        if (slot.isArmor() && walkers$blockEquipSlot(slot)) {
            cir.setReturnValue(ItemStack.EMPTY); // might be unstable!! - Pretend there is no item in that slot
        }
    }

    @Inject(method = "canUseSlot", at = @At("HEAD"), cancellable = true)
    private void onCanUseSlot(@NotNull EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
        if (slot.isArmor() && walkers$blockEquipSlot(slot)) {
            cir.setReturnValue(false); // prevent equipping
        }
    }

    @Unique
    private boolean walkers$blockEquipSlot(EquipmentSlot slot) {
        if ((Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape == null) return false;

            String shapeKey = EntityType.getKey(shape.getType()).toString();

            Set<String> blockedSlots = new HashSet<>(walkers$getBlockedSlots("*"));
            blockedSlots.addAll(walkers$getBlockedSlots(shapeKey));

            return blockedSlots.contains(slot.getName());
        }
        return false;
    }

    @Unique
    private List<String> walkers$getBlockedSlots(String key) {
        return Walkers.CONFIG.blockEquipmentSlots.getOrDefault(key, Collections.emptyList());
    }

}