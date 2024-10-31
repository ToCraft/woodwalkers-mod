package tocraft.walkers.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.traits.ShapeTrait;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.FearedTrait;
import tocraft.walkers.traits.impl.PreyTrait;

import java.util.Map;
import java.util.function.Predicate;

@SuppressWarnings("DataFlowIssue")
@Mixin(Mob.class)
public class MobHunterPreyMixin {
    @Shadow
    @Final
    protected GoalSelector targetSelector;
    @Shadow
    @Final
    protected GoalSelector goalSelector;

    @Inject(method = "<init>", at = @At("TAIL"))
    protected void registerCustomGoals(CallbackInfo ci) {
        // ensure hunter can attack players with a shape similar to their normal prey
        if (!Walkers.CONFIG.hunterAttackAsPreyMorphedPlayer) {
            return;
        } else {
            for (Map.Entry<ShapeTrait<?>, Predicate<LivingEntity>> trait : TraitRegistry.getAllRegisteredById(PreyTrait.ID).entrySet()) {
                PreyTrait<?> preyTrait = (PreyTrait<?>) trait.getKey();

                if (preyTrait.isHunter((Mob) (Object) this)) {
                    targetSelector.addGoal(preyTrait.getPriority(), new NearestAttackableTargetGoal<>((Mob) (Object) this, Player.class, preyTrait.getRandInt(), false, false, player -> {
                        LivingEntity shape = PlayerShape.getCurrentShape((Player) player);
                        return shape != null && trait.getValue().test(shape);
                    }));
                }
            }
        }

        if ((Object) this instanceof PathfinderMob mob) {
            for (Map.Entry<ShapeTrait<?>, Predicate<LivingEntity>> trait : TraitRegistry.getAllRegisteredById(FearedTrait.ID).entrySet()) {
                FearedTrait<?> fearedTrait = (FearedTrait<?>) trait.getKey();

                if (fearedTrait.isFeared(mob)) {
                    goalSelector.addGoal(fearedTrait.getPriority(), new AvoidEntityGoal<>(mob, Player.class, player -> {
                        LivingEntity shape = PlayerShape.getCurrentShape((Player) player);
                        return shape != null && trait.getValue().test(shape);
                    },
                            6.0F,
                            1.0D,
                            1.2D,
                            player -> true
                    ));
                }
            }
        }
    }
}
