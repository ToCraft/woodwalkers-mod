package tocraft.walkers.mixin.player;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.FlightHelper;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.mixin.EntityTrackerAccessor;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.RiderTrait;

import java.util.Optional;

@SuppressWarnings("UnreachableCode")
@Mixin(Player.class)
public abstract class PlayerEntityDataMixin extends LivingEntity implements PlayerDataProvider {

    @Unique
    private static final String ABILITY_COOLDOWN_KEY = "AbilityCooldown";
    @Unique
    @Nullable
    private ShapeType<?> walkers$unlocked;
    @Unique
    private int walkers$remainingTime = 0;
    @Unique
    private int walkers$abilityCooldown = 0;
    @Unique
    @Nullable
    private LivingEntity walkers$shape = null;

    private PlayerEntityDataMixin(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void readNbt(CompoundTag tag, CallbackInfo info) {
        // This is the new tag for saving Walkers unlock information.
        // It includes metadata for variants.
        CompoundTag unlockedShape = tag.getCompound("UnlockedShape");
        this.walkers$unlocked = ShapeType.from(unlockedShape);

        // Abilities
        walkers$abilityCooldown = tag.getInt(ABILITY_COOLDOWN_KEY);

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
        }

        // put entity type ID under the key "id", or "minecraft:empty" if no shape is
        // equipped (or the shape entity type is invalid)
        tag.putString("id",
                walkers$shape == null ? "minecraft:empty" : EntityType.getKey(walkers$shape.getType()).toString());
        tag.put("EntityData", entityTag);
        return tag;
    }

    @Unique
    public void walkers$readCurrentShape(CompoundTag tag) {
        Optional<EntityType<?>> type = EntityType.by(tag);

        // set shape to null (no shape) if the entity id is "minecraft:empty"
        if (tag.getString("id").equals("minecraft:empty")) {
            this.walkers$shape = null;
            this.refreshDimensions();
        }

        // if entity type was valid, deserialize entity data from tag
        else if (type.isPresent()) {
            CompoundTag entityTag = tag.getCompound("EntityData");

            // ensure entity data exists
            if (!entityTag.isEmpty()) {
                if (walkers$shape == null || !type.get().equals(walkers$shape.getType())) {
                    walkers$shape = (LivingEntity) type.get().create(this.level(), EntitySpawnReason.LOAD);

                    // refresh player dimensions/hitbox on client
                    this.refreshDimensions();
                }

                walkers$shape.load(entityTag);
            }
        }
    }

    @Unique
    @Override
    @Nullable
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
    @Nullable
    public LivingEntity walkers$getCurrentShape() {
        return walkers$shape;
    }

    @Unique
    @Override
    public void walkers$setCurrentShape(LivingEntity shape) {
        this.walkers$shape = shape;
    }

    @SuppressWarnings("ConstantConditions")
    @Unique
    @Override
    public void walkers$updateShapes(@Nullable LivingEntity shape) {
        Player player = (Player) (Object) this;
        AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance armorAttribute = player.getAttribute(Attributes.ARMOR);
        AttributeInstance armorToughnessAttribute = player.getAttribute(Attributes.ARMOR_TOUGHNESS);

        this.walkers$shape = shape;

        // shape is valid and scaling health is on; set entity's max health and current
        // health to reflect shape.
        if (shape != null) {
            if (Walkers.CONFIG.scalingHealth && healthAttribute != null) {
                // calculate the current health in percentage, used later
                float currentHealthPercent = player.getHealth() / player.getMaxHealth();

                healthAttribute.setBaseValue(Math.min(Walkers.CONFIG.maxHealth, shape.getMaxHealth()));

                // set health
                if (Walkers.CONFIG.percentScalingHealth) {
                    player.setHealth(Math.min(currentHealthPercent * player.getMaxHealth(), player.getMaxHealth()));
                } else {
                    player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
                }
            }
            if (Walkers.CONFIG.scalingAmor) {
                AttributeInstance shapeArmorAttribute = shape.getAttribute(Attributes.ARMOR);
                if (armorAttribute != null && shapeArmorAttribute != null) {
                    armorAttribute.setBaseValue(Math.min(Walkers.CONFIG.maxAmor, shapeArmorAttribute.getBaseValue()));
                }
                AttributeInstance shapeArmorToughnessAttribute = shape.getAttribute(Attributes.ARMOR_TOUGHNESS);
                if (armorToughnessAttribute != null && shapeArmorToughnessAttribute != null) {
                    armorToughnessAttribute.setBaseValue(Math.min(Walkers.CONFIG.maxAmorToughness, shapeArmorToughnessAttribute.getBaseValue()));
                }
            }

            AttributeInstance playerScaleAttribute = player.getAttribute(Attributes.SCALE);
            AttributeInstance shapeScaleAttribute = shape.getAttribute(Attributes.SCALE);
            if (playerScaleAttribute != null && shapeScaleAttribute != null) {
                shapeScaleAttribute.setBaseValue(playerScaleAttribute.getBaseValue());
            }

            if (Walkers.CONFIG.scalingStepHeight) {
                AttributeInstance playerStepHeightAttr = player.getAttribute(Attributes.STEP_HEIGHT);

                if (playerStepHeightAttr != null) {
                    playerStepHeightAttr.setBaseValue(shape.maxUpStep());
                }
            }
        }

        // refresh entity hitbox dimensions
        player.refreshDimensions();

        // If the shape is null (going back to player), set the player's base health
        // value to 20 (default) to clear old changes.
        if (shape == null) {
            if (Walkers.CONFIG.scalingHealth && healthAttribute != null) {
                healthAttribute.setBaseValue(20);

                // Clear health value if needed
                if (Walkers.CONFIG.percentScalingHealth) {
                    float currentHealthPercent = player.getHealth() / player.getMaxHealth();

                    player.setHealth(Math.min(currentHealthPercent * player.getMaxHealth(), player.getMaxHealth()));
                } else {
                    player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
                }
            }

            if (Walkers.CONFIG.scalingAmor) {
                if (armorAttribute != null) {
                    armorAttribute.setBaseValue(0);
                }
                if (armorToughnessAttribute != null) {
                    armorAttribute.setBaseValue(0);
                }
            }

            if (Walkers.CONFIG.scalingStepHeight) {
                AttributeInstance playerStepHeightAttr = player.getAttribute(Attributes.STEP_HEIGHT);

                if (playerStepHeightAttr != null) {
                    playerStepHeightAttr.setBaseValue(0.6);
                }
            }
        }

        // update flight properties on player depending on shape
        ServerPlayer serverPlayer = (ServerPlayer) player;
        if (Walkers.hasFlyingPermissions((ServerPlayer) player)) {
            FlightHelper.grantFlightTo(serverPlayer);
            FlightHelper.updateFlyingSpeed(player);
            player.onUpdateAbilities();
        } else if (!player.isCreative()) {
            FlightHelper.revokeFlight(serverPlayer);
            player.getAbilities().setFlyingSpeed(0.05f);
            player.onUpdateAbilities();
        }

        // If the player is riding a Ravager and changes into a Walkers that cannot
        // ride Ravagers, kick them off.
        if (player.getVehicle() instanceof LivingEntity livingVehicle) {
            // checks, if the player can continue riding
            boolean b1 = false;
            boolean b2 = false;
            for (RiderTrait<?> riderTrait : TraitRegistry.get(shape, RiderTrait.ID).stream().map(entry -> (RiderTrait<?>) entry).toList()) {
                if (riderTrait.isRideable(livingVehicle) || (livingVehicle instanceof Player rideablePlayer && riderTrait.isRideable(PlayerShape.getCurrentShape(rideablePlayer)))) {
                    b1 = true;
                    b2 = true;
                }
                if (b2) break;
            }
            if (!b1) {
                player.stopRiding();
            }
        }

        // If the player is riding another Player that switches into another shape that cannot
        // be ridden, the riding player stops riding
        if (!(shape instanceof AbstractHorse)) {
            for (Entity passenger : player.getPassengers()) {
                passenger.stopRiding();
            }
        }

        // sync with client
        if (!player.level().isClientSide) {
            PlayerShape.sync((ServerPlayer) player);

            Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) ((ServerLevel) player.level())
                    .getChunkSource().chunkMap).getEntityMap();
            Object tracking = trackers.get(player.getId());
            ((EntityTrackerAccessor) tracking).getSeenBy().forEach(
                    listener -> PlayerShape.sync((ServerPlayer) player, listener.getPlayer())
            );
        }
    }
}
