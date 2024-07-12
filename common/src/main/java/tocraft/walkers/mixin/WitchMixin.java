package tocraft.walkers.mixin;

//#if MC>=1205
import net.minecraft.core.Holder;
//#endif
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import tocraft.walkers.api.PlayerShape;

@SuppressWarnings("unused")
@Mixin(Witch.class)
public class WitchMixin {
    //#if MC>=1205
        @ModifyArg(method = "performRangedAttack(Lnet/minecraft/world/entity/LivingEntity;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionContents;createItemStack(Lnet/minecraft/world/item/Item;Lnet/minecraft/core/Holder;)Lnet/minecraft/world/item/ItemStack;"), index = 1)
    private Holder<Potion> onSetPotionInThrowPotion(Holder<Potion> potion) {
    //#else
    //$$ @ModifyArg(method = "performRangedAttack(Lnet/minecraft/world/entity/LivingEntity;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionUtils;setPotion(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/alchemy/Potion;)Lnet/minecraft/world/item/ItemStack;"), index = 1)
    //$$ private Potion onSetPotionInThrowPotion(Potion potion) {
    //#endif
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
