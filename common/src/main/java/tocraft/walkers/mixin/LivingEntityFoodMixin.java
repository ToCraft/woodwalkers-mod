package tocraft.walkers.mixin;

import com.google.common.base.Suppliers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tocraft.walkers.api.PlayerShape;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFoodMixin extends Entity {

    @Unique
    private static final Supplier<List<FoodProperties>> WOLVES_IGNORE = Suppliers.memoize(() -> Arrays.asList(Foods.CHICKEN, Foods.PUFFERFISH, Foods.ROTTEN_FLESH));

    public LivingEntityFoodMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(
            method = "addEatEffect",
            at = @At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void removeFleshHungerForWolves(FoodProperties foodProperties, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            // If this player is a Wolf and the item they are eating is an item wolves are immune to, cancel the method.
            if (shape instanceof Wolf) {
                if (WOLVES_IGNORE.get().contains(foodProperties)) {
                    ci.cancel();
                }
            }
        }
    }
}
