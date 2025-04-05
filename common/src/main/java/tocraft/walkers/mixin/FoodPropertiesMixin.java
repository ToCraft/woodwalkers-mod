package tocraft.walkers.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.api.PlayerShape;

import net.minecraft.world.entity.animal.sheep.Sheep;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {
    @Inject(method = "onConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"))
    private void regenerateWoolFromFood(Level level, LivingEntity entity, ItemStack itemStack, Consumable consumable, CallbackInfo ci) {
        if (entity instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape instanceof Sheep sheepShape) {
                if (sheepShape.isSheared())
                    sheepShape.setSheared(false);
            }
        }
    }

    @Inject(method = "onConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"))
    private void dieFromCookies(Level level, LivingEntity entity, ItemStack itemStack, Consumable consumable, CallbackInfo ci) {
        if (entity instanceof Player player && level instanceof ServerLevel serverLevel && itemStack.is(Items.COOKIE)) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);
            if (shape instanceof Parrot) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 900));
                if (player.isCreative() || !entity.isInvulnerable()) {
                    entity.hurtServer(serverLevel, level.damageSources().playerAttack(player), Float.MAX_VALUE);
                }
            }
        }
    }
}
