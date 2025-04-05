package tocraft.walkers.mixin;

import com.google.common.base.Suppliers;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import tocraft.walkers.api.PlayerShape;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(Consumable.class)
public class ConsumableMixin {
    @Unique
    private static final Supplier<List<Item>> WOLVES_IGNORE = Suppliers.memoize(() -> Arrays.asList(Items.CHICKEN, Items.PUFFERFISH, Items.ROTTEN_FLESH));

    @WrapWithCondition(method = "onConsume", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
    private boolean onConsume(List<ConsumeEffect> instance, Consumer<? super ConsumeEffect> consumer, @Local(argsOnly = true, ordinal = 0) LivingEntity entity, @Local(argsOnly = true, ordinal = 0) ItemStack stack) {
        if (entity instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            // If this player is a Wolf and the item they are eating is an item wolves are immune to, cancel the method.
            if (shape instanceof Wolf) {
                return !WOLVES_IGNORE.get().contains(stack.getItem());
            }
        }
        return true;
    }
}
