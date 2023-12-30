package tocraft.walkers.mixin.player;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.craftedcore.events.Event;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.FlightHelper;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.event.ShapeEvents;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.DimensionsRefresher;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.mixin.EntityTrackerAccessor;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;
import tocraft.walkers.registry.WalkersEntityTags;

import java.util.Optional;

@Mixin(Player.class)
public abstract class PlayerEntityDataMixin extends LivingEntity implements PlayerDataProvider {

	@Shadow
	public abstract void playSound(SoundEvent sound, float volume, float pitch);

	@Unique
	private static final String ABILITY_COOLDOWN_KEY = "AbilityCooldown";
	@Unique
	private ShapeType<?> walkers$unlocked;
	@Unique
	private int walkers$remainingTime = 0;
	@Unique
	private int walkers$abilityCooldown = 0;
	@Unique
	private LivingEntity walkers$shape = null;
	@Unique
	private ShapeType<?> walkers$shapeType = null;

	private PlayerEntityDataMixin(EntityType<? extends LivingEntity> type, Level world) {
		super(type, world);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
	private void readNbt(CompoundTag tag, CallbackInfo info) {
		// This is the new tag for saving Walkers unlock information.
		// It includes metadata for variants.
		CompoundTag unlockedShape = tag.getCompound("UnlockedShape");
        this.walkers$unlocked = ShapeType.from(unlockedShape);

        int newCooldown = tag.getInt(ABILITY_COOLDOWN_KEY);
        if (Walkers.foundPotionAbilities) {
        	newCooldown = tag.getCompound("ycdm").getInt("cooldown");
        }
		// Abilities
		walkers$abilityCooldown = newCooldown;

		// Hostility
		walkers$remainingTime = tag.getInt("RemainingHostilityTime");

		// Current Walkers
		walkers$readCurrentShape(tag.getCompound("CurrentShape"));
	}

	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	private void writeNbt(CompoundTag tag, CallbackInfo info) {
		// Write 'Unlocked' Walkers data
		CompoundTag id = new CompoundTag();
		if (walkers$unlocked != null)
			id = walkers$unlocked.writeCompound();
		tag.put("UnlockedShape", id);

		// Abilities
		tag.putInt(ABILITY_COOLDOWN_KEY, walkers$abilityCooldown);

		// Hostility
		tag.putInt("RemainingHostilityTime", walkers$remainingTime);

		// Current Walkers
		tag.put("CurrentShape", walkers$writeCurrentShape(new CompoundTag()));
	}

	@Unique
	private CompoundTag walkers$writeCurrentShape(CompoundTag tag) {
		CompoundTag entityTag = new CompoundTag();

		// serialize current shapeAttackDamage data to tag if it exists
		if (walkers$shape != null) {
			walkers$shape.saveWithoutId(entityTag);
			if (walkers$shapeType != null) {
				walkers$shapeType.writeEntityNbt(entityTag);
			}
		}

		// put entity type ID under the key "id", or "minecraft:empty" if no shape is
		// equipped (or the shape entity type is invalid)
		tag.putString("id",
				walkers$shape == null ? "minecraft:empty" : Registry.ENTITY_TYPE.getKey(walkers$shape.getType()).toString());
		tag.put("EntityData", entityTag);
		return tag;
	}

	@Unique
	public void walkers$readCurrentShape(CompoundTag tag) {
		Optional<EntityType<?>> type = EntityType.by(tag);

		// set shape to null (no shape) if the entity id is "minecraft:empty"
		if (tag.getString("id").equals("minecraft:empty")) {
			this.walkers$shape = null;
			((DimensionsRefresher) this).shape_refreshDimensions();
		}

		// if entity type was valid, deserialize entity data from tag
		else if (type.isPresent()) {
			CompoundTag entityTag = tag.getCompound("EntityData");

			// ensure entity data exists
			if (entityTag != null) {
				if (walkers$shape == null || !type.get().equals(walkers$shape.getType())) {
					walkers$shape = (LivingEntity) type.get().create(level);

					// refresh player dimensions/hitbox on client
					((DimensionsRefresher) this).shape_refreshDimensions();
				}

				walkers$shape.load(entityTag);
				walkers$shapeType = ShapeType.fromEntityNbt(tag);
			}
		}
	}

	@Unique
	@Override
	public ShapeType<?> walkers$get2ndShape() {
		return walkers$unlocked;
	}

	@Override
	public void walkers$set2ndShape(ShapeType<?> unlocked) {
		this.walkers$unlocked = unlocked;
	}

	@Unique
	@Override
	public int walkers$getRemainingHostilityTime() {
		return walkers$remainingTime;
	}

	@Unique
	@Override
	public void walkers$setRemainingHostilityTime(int max) {
		walkers$remainingTime = max;
	}

	@Unique
	@Override
	public int walkers$getAbilityCooldown() {
		return walkers$abilityCooldown;
	}

	@Unique
	@Override
	public void walkers$setAbilityCooldown(int abilityCooldown) {
		this.walkers$abilityCooldown = abilityCooldown;
	}

	@Unique
	@Override
	public LivingEntity walkers$getCurrentShape() {
		return walkers$shape;
	}

	@Override
	public ShapeType<?> walkers$getCurrentShapeType() {
		return walkers$shapeType;
	}

	@Unique
	@Override
	public void walkers$setCurrentShape(LivingEntity shape) {
		this.walkers$shape = shape;
	}

	@Unique
	@Override
	public boolean walkers$updateShapes(@Nullable LivingEntity shape) {
		Player player = (Player) (Object) this;
		AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
		AttributeInstance attackAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
		Event.Result result = ShapeEvents.SWAP_SHAPE.invoker().swap((ServerPlayer) player, shape);
		if (result.isFalse()) {
			return false;
		}

		this.walkers$shape = shape;

		// refresh entity hitbox dimensions
		((DimensionsRefresher) player).shape_refreshDimensions();

		// shape is valid and scaling health is on; set entity's max health and current
		// health to reflect shape.
		if (shape != null) {
			if (Walkers.CONFIG.scalingHealth && healthAttribute != null) {
				// calculate the current health in percentage, used later
				float currentHealthPercent = player.getHealth() / player.getMaxHealth();

				healthAttribute.setBaseValue(Math.min(Walkers.CONFIG.maxHealth, shape.getMaxHealth()));

				// set health
				if (Walkers.CONFIG.percentScalingHealth)
					player.setHealth(Math.min(currentHealthPercent * player.getMaxHealth(), player.getMaxHealth()));
				else
					player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
			}
			if (Walkers.CONFIG.scalingAttackDamage && attackAttribute != null) {
				// get shape attack damage, return 1D if value is lower or not existing
				double shapeAttackDamage = 1D;
				try {
					shapeAttackDamage = Math.max(shape.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue(),
							shapeAttackDamage);
				} catch (Exception ignored) {

				}
				attackAttribute.setBaseValue(Math.min(Walkers.CONFIG.maxAttackDamage, shapeAttackDamage));
			}
		}

		// If the shape is null (going back to player), set the player's base health
		// value to 20 (default) to clear old changes.
		if (shape == null) {
			float currentHealthPercent = player.getHealth() / player.getMaxHealth();

			if (Walkers.CONFIG.scalingHealth && healthAttribute != null) {
				healthAttribute.setBaseValue(20);
			}

			if (Walkers.CONFIG.scalingAttackDamage && attackAttribute != null) {
				attackAttribute.setBaseValue(1D);
			}

			// Clear health value if needed
			if (Walkers.CONFIG.percentScalingHealth)
				player.setHealth(Math.min(currentHealthPercent * player.getMaxHealth(), player.getMaxHealth()));
			else
				player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
		}

		// update flight properties on player depending on shape
		ServerPlayer serverPlayer = (ServerPlayer) player;
		if (Walkers.hasFlyingPermissions((ServerPlayer) player)) {
			FlightHelper.grantFlightTo(serverPlayer);
			player.getAbilities().setFlyingSpeed(Walkers.CONFIG.flySpeed);
			player.onUpdateAbilities();
		} else {
			FlightHelper.revokeFlight(serverPlayer);
			player.getAbilities().setFlyingSpeed(0.05f);
			player.onUpdateAbilities();
		}

		// If the player is riding a Ravager and changes into a Walkers that cannot
		// ride Ravagers, kick them off.
		if (player.getVehicle() instanceof Ravager
				&& (shape == null || !shape.getType().is(WalkersEntityTags.RAVAGER_RIDING))) {
			player.stopRiding();
		}

		// sync with client
		if (!player.level.isClientSide) {
			PlayerShape.sync((ServerPlayer) player);

			Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) ((ServerLevel) player.level)
					.getChunkSource().chunkMap).getEntityMap();
			Object tracking = trackers.get(player.getId());
			((EntityTrackerAccessor) tracking).getSeenBy().forEach(
					listener -> PlayerShape.sync((ServerPlayer) player, listener.getPlayer())
			);
		}

		return true;
	}
}
