package tocraft.walkers.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.FearedSkill;
import tocraft.walkers.skills.impl.PreySkill;

import java.util.function.Predicate;

@Mixin(Mob.class)
public class MobHunterPreyMixin {
    @Shadow
    @Final
    protected GoalSelector targetSelector;
    @Shadow
    @Final
    protected GoalSelector goalSelector;

    @Unique
    private boolean walkers$registeredAttackPreyGoals = false;
    @Unique
    private boolean walkers$registeredAvoidHunterGoals = false;

    @Inject(method = "tick", at = @At("HEAD"))
    public void registerGoals(CallbackInfo ci) {
        if (!walkers$registeredAttackPreyGoals) {
            // ensure hunter can attack players with a shape similar to their normal prey
            if (!Walkers.CONFIG.hunterAttackAsPreyMorphedPlayer) {
                return;
            } else {
                targetSelector.addGoal(7,
                        new NearestAttackableTargetGoal<>((Mob) (Object) this, Player.class, 10, false, false, player -> {
                            // hunter should ignore players that look like their prey if they have an owner,
                            // unless the config option is turned to true
                            if (!Walkers.CONFIG.ownedHunterAttackAsPreyMorphedPlayer && (Mob) (Object) this instanceof TamableAnimal tamableAnimal && tamableAnimal.getOwner() != null) {
                                return false;
                            }

                            LivingEntity shape = PlayerShape.getCurrentShape((Player) player);

                            if (shape != null) {
                                for (PreySkill<?> preySkill : SkillRegistry.get(shape, PreySkill.ID).stream().map(entry -> (PreySkill<?>) entry).toList()) {
                                    for (Predicate<LivingEntity> hunterPredicate : preySkill.hunter) {
                                        if (hunterPredicate.test((Mob) (Object) this)) return true;
                                    }
                                }
                            }

                            return false;
                        }));
            }

            walkers$registeredAttackPreyGoals = true;
        }

        if (!walkers$registeredAvoidHunterGoals && (Object) this instanceof PathfinderMob mob) {
            goalSelector.addGoal(3, new AvoidEntityGoal<>(
                    mob,
                    Player.class,
                    player -> {
                        LivingEntity shape = PlayerShape.getCurrentShape((Player) player);
                        if (shape != null) {
                            for (FearedSkill<?> fearedSkill : SkillRegistry.get(shape, FearedSkill.ID).stream().map(entry -> (FearedSkill<?>) entry).toList()) {
                                for (Predicate<LivingEntity> preyPredicate : fearedSkill.fearful) {
                                    if (preyPredicate.test(mob)) return true;
                                }
                            }
                        }
                        return false;

                    },
                    6.0F,
                    1.0D,
                    1.2D,
                    player -> true
            ));
            walkers$registeredAvoidHunterGoals = true;
        }
    }
}
