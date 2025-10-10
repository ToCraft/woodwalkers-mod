package dev.tocraft.walkers.mixin;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.api.PlayerHostility;
import dev.tocraft.walkers.api.PlayerShape;
import dev.tocraft.walkers.integrations.Integrations;
import dev.tocraft.walkers.traits.TraitRegistry;
import dev.tocraft.walkers.traits.impl.FearedTrait;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class ActiveTargetGoalMixin extends TrackTargetGoalMixin {

    @Shadow
    protected LivingEntity target;

    @Inject(method = "start", at = @At("HEAD"), cancellable = true)
    private void ignoreShapedPlayers(CallbackInfo ci) {
        if (Walkers.CONFIG.hostilesIgnoreHostileShapedPlayer && this.mob instanceof Enemy
                && this.target instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null && Integrations.mightAttackInnocent(this.mob, player)) {
                boolean hasHostility = PlayerHostility.hasHostility(player);

                // only cancel if the player does not have hostility
                if (!hasHostility) {
                    // prey should ignore hunter
                    for (FearedTrait<?> fearedTrait : TraitRegistry.get(shape, FearedTrait.ID).stream().map(entry -> (FearedTrait<?>) entry).toList()) {
                        if (fearedTrait.isFeared(mob)) {
                            this.stop();
                            ci.cancel();
                        }
                    }

                    // polar bears should ignore polar bears
                    if (this.mob instanceof PolarBear && shape.getType().equals(EntityType.POLAR_BEAR)) {
                        this.stop();
                        ci.cancel();
                    }

                    // withers should ignore undead
                    else if (this.mob instanceof WitherBoss && shape.getType().getCategory().equals(MobCategory.MONSTER)) {
                        this.stop();
                        ci.cancel();
                    }

                    // hostile mobs (besides wither) should not target players morphed as hostile
                    // mobs
                    else if (!(this.mob instanceof WitherBoss) && (shape instanceof Enemy || Walkers.CONFIG.hostilesIgnoreNotHostileShapedPlayer)) {
                        // endermen should attack endermites
                        if (this.mob instanceof EnderMan && shape.getType().equals(EntityType.ENDERMITE)) {
                            return;
                        }

                        // Wither Skeletons should attack Piglins
                        else if (this.mob instanceof WitherSkeleton && shape instanceof AbstractPiglin) {
                            return;
                        }

                        this.stop();
                        ci.cancel();
                    }
                }
            }
        }
    }

    @Override
    protected void shape_shouldContinue(CallbackInfoReturnable<Boolean> cir) {
        // check cancelling for hostiles
        if ((Walkers.CONFIG.hostilesIgnoreHostileShapedPlayer) && Walkers.CONFIG.hostilesForgetNewShapedPlayer
                && this.mob instanceof Enemy && this.target instanceof Player player) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null && Integrations.mightAttackInnocent(this.mob, player)) {
                boolean hasHostility = PlayerHostility.hasHostility(player);

                // only cancel if the player does not have hostility
                if (!hasHostility) {
                    // prey should ignore hunter
                    for (FearedTrait<?> fearedTrait : TraitRegistry.get(shape, FearedTrait.ID).stream().map(entry -> (FearedTrait<?>) entry).toList()) {
                        if (fearedTrait.isFeared(mob)) {
                            cir.setReturnValue(false);
                            return;
                        }
                    }

                    // withers should ignore undead
                    if (this.mob instanceof WitherBoss && shape.getType().getCategory().equals(MobCategory.MONSTER)) {
                        cir.setReturnValue(false);
                    }

                    // hostile mobs (besides wither) should not target players morphed as hostile
                    // mobs
                    else if (!(this.mob instanceof WitherBoss) && (shape instanceof Enemy || Walkers.CONFIG.hostilesIgnoreNotHostileShapedPlayer)) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }

    @Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
    private void onCanUse(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (this.target instanceof Player player && PlayerShape.getCurrentShape(player) instanceof PolarBear) {
                cir.setReturnValue(false);
            }
        }
    }
}
