package tocraft.walkers.mixin;

import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import tocraft.walkers.api.PlayerShape;

@Mixin(Witch.class)
public class WitchMixin {
    @ModifyArg(method = "performRangedAttack(Lnet/minecraft/world/entity/LivingEntity;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionUtils;setPotion(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/alchemy/Potion;)Lnet/minecraft/world/item/ItemStack;"), index = 1)
    private Potion onSetPotionInThrowPotion(Potion potion) {
        if (((Witch) (Object) this).getTarget() instanceof Player player && PlayerShape.getCurrentShape(player) instanceof Raider) {
            if (player.getHealth() <= 4.0F) {
                return Potions.HEALING;
            } else {
                return Potions.REGENERATION;
            }
        }
        return potion;
    }
}
