package tocraft.walkers.mixin;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.registry.WalkersEntityTags;

@Mixin(Fox.class)
public abstract class FoxEntityMixin extends Animal {

	@Shadow
	@Final
	@Mutable
	private static Predicate<Entity> AVOID_PLAYERS;

	private FoxEntityMixin(EntityType<? extends Animal> entityType, Level world) {
		super(entityType, world);
	}

	// Change the default "flee from player," predicate to ignore players disguised
	// as Foxes.
	// Hopefully nobody else needs to modify fox fleeing behavior.
	static {
		AVOID_PLAYERS = entity -> {
			boolean isShapedPlayer = false;

			if (entity instanceof Player player) {
				LivingEntity shape = PlayerShape.getCurrentShape(player);
				if (shape instanceof Fox) {
					isShapedPlayer = true;
				}
			}

			return !entity.isDiscrete() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) && !isShapedPlayer;
		};
	}

	@Inject(method = "registerGoals", at = @At("RETURN"))
	private void addPlayerTarget(CallbackInfo ci) {
		this.targetSelector.addGoal(7,
				new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false, player -> {
					// ensure foxes can attack players with an shape similar to their normal prey
					if (!Walkers.CONFIG.foxesAttack2ndShapedPrey()) {
						return false;
					}

					// foxes can target players if their shape is in the fox_prey tag, or if they
					// are an entity that extends FishEntity
					// todo: add baby turtle targeting
					LivingEntity shape = PlayerShape.getCurrentShape((Player) player);
					return shape != null && shape.getType().is(WalkersEntityTags.FOX_PREY)
							|| shape instanceof AbstractFish;
				}));
	}
}
