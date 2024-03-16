package tocraft.walkers.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerHostility;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.skills.SkillRegistry;
import tocraft.walkers.api.skills.impl.HunterSkill;
import tocraft.walkers.integrations.Integrations;

import java.util.function.Predicate;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class ActiveTargetGoalMixin extends TrackTargetGoalMixin {

    @Shadow
    protected LivingEntity target;

    @Inject(method = "start", at = @At("HEAD"), cancellable = true)
    private void ignoreShapedPlayers(CallbackInfo ci) {
        if (Walkers.CONFIG.hostilesIgnoreHostileShapedPlayer && this.mob instanceof Enemy
                && this.target instanceof Player player && Integrations.mightAttackInnocent(this.mob, player)) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                boolean hasHostility = PlayerHostility.hasHostility(player);

                // only cancel if the player does not have hostility
                if (!hasHostility) {
                    // prey should ignore hunter
                    for (HunterSkill<?> hunterSkill : SkillRegistry.get(shape, HunterSkill.ID).stream().map(entry -> (HunterSkill<?>) entry).toList()) {
                        for (Predicate<LivingEntity> preyPredicate : hunterSkill.prey) {
                            if (preyPredicate.test(mob)) {
                                this.stop();
                                ci.cancel();
                            }
                        }
                    }

                    // polar bears should ignore polar bears
                    if (this.mob instanceof PolarBear && shape.getType().equals(EntityType.POLAR_BEAR)) {
                        this.stop();
                        ci.cancel();
                    }

                    // withers should ignore undead
                    else if (this.mob instanceof WitherBoss && shape.getMobType().equals(MobType.UNDEAD)) {
                        this.stop();
                        ci.cancel();
                    }

                    // hostile mobs (besides wither) should not target players morphed as hostile
                    // mobs
                    else if (!(this.mob instanceof WitherBoss) && shape instanceof Enemy) {
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
        if (Walkers.CONFIG.hostilesIgnoreHostileShapedPlayer && Walkers.CONFIG.hostilesForgetNewHostileShapedPlayer
                && this.mob instanceof Enemy && this.target instanceof Player player && Integrations.mightAttackInnocent(this.mob, player)) {
            LivingEntity shape = PlayerShape.getCurrentShape(player);

            if (shape != null) {
                boolean hasHostility = PlayerHostility.hasHostility(player);

                // only cancel if the player does not have hostility
                if (!hasHostility) {
                    // creepers should ignore cats
                    if (this.mob instanceof Creeper && shape.getType().equals(EntityType.OCELOT)) {
                        cir.setReturnValue(false);
                    }

                    // skeletons should ignore wolfs
                    if (this.mob instanceof Skeleton && shape.getType().equals(EntityType.WOLF)) {
                        cir.setReturnValue(false);
                    }

                    // withers should ignore undead
                    else if (this.mob instanceof WitherBoss && shape.getMobType().equals(MobType.UNDEAD)) {
                        cir.setReturnValue(false);
                    }

                    // hostile mobs (besides wither) should not target players morphed as hostile
                    // mobs
                    else if (!(this.mob instanceof WitherBoss) && shape instanceof Enemy) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
}
