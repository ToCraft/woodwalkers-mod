package tocraft.walkers.impl;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.skills.SkillRegistry;
import tocraft.walkers.api.skills.impl.PreySkill;

import java.util.function.Predicate;

public class HunterProvider {
    public static void addPlayerPreyAttackTarget(Mob mob, GoalSelector targetSelector) {
        targetSelector.addGoal(7,
                new NearestAttackableTargetGoal<>(mob, Player.class, 10, false, false, player -> {
                    // ensure hunter can attack players with a shape similar to their normal prey
                    if (!Walkers.CONFIG.hunterAttackAsPreyMorphedPlayer) {
                        return false;
                    }

                    LivingEntity shape = PlayerShape.getCurrentShape((Player) player);

                    // hunter should ignore players that look like their prey if they have an owner,
                    // unless the config option is turned to true
                    if (!Walkers.CONFIG.ownedHunterAttackAsPreyMorphedPlayer && mob instanceof TamableAnimal tamableAnimal && tamableAnimal.getOwner() != null) {
                        return false;
                    }

                    if (shape != null) {
                        for (PreySkill<?> preySkill : SkillRegistry.get(shape, PreySkill.ID).stream().map(entry -> (PreySkill<?>) entry).toList()) {
                            for (Predicate<LivingEntity> hunterPredicate : preySkill.hunter) {
                                if (hunterPredicate.test(mob)) return true;
                            }
                        }
                    }

                    return false;
                }));
    }
}
