package tocraft.walkers.mixin.player;

import dev.architectury.event.EventResult;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.event.WalkersSwapCallback;
import tocraft.walkers.api.FlightHelper;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.DimensionsRefresher;
import tocraft.walkers.impl.PlayerDataProvider;
import tocraft.walkers.mixin.EntityTrackerAccessor;
import tocraft.walkers.mixin.ThreadedAnvilChunkStorageAccessor;
import tocraft.walkers.registry.WalkersEntityTags;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityDataMixin extends LivingEntity implements PlayerDataProvider {

    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);
    @Unique private static final String ABILITY_COOLDOWN_KEY = "AbilityCooldown";
    @Unique private ShapeType<?> unlocked;
    @Unique private int remainingTime = 0;
    @Unique private int abilityCooldown = 0;
    @Unique private LivingEntity shape = null;
    @Unique private ShapeType<?> shapeType = null;

    private PlayerEntityDataMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void readNbt(NbtCompound tag, CallbackInfo info) {
        // This is the new tag for saving Walkers unlock information.
        // It includes metadata for variants.
        NbtCompound unlockedShape = tag.getCompound("UnlockedShape");
        ShapeType<?> type = ShapeType.from(unlockedShape);
        this.unlocked = type;

        // Abilities
        abilityCooldown = tag.getInt(ABILITY_COOLDOWN_KEY);

        // Hostility
        remainingTime = tag.getInt("RemainingHostilityTime");

        // Current Walkers
        readCurrentShape(tag.getCompound("CurrentShape"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void writeNbt(NbtCompound tag, CallbackInfo info) {
        // Write 'Unlocked' Walkers data
        NbtCompound id = new NbtCompound();
        if (unlocked != null)
            id = unlocked.writeCompound();
        tag.put("UnlockedShape", id);
        
        // Abilities
        tag.putInt(ABILITY_COOLDOWN_KEY, abilityCooldown);

        // Hostility
        tag.putInt("RemainingHostilityTime", remainingTime);

        // Current Walkers
        tag.put("CurrentShape", writeCurrentShape(new NbtCompound()));
    }

    @Unique
    private NbtCompound writeCurrentShape(NbtCompound tag) {
        NbtCompound entityTag = new NbtCompound();

        // serialize current shapeAttackDamage data to tag if it exists
        if(shape != null) {
            shape.writeNbt(entityTag);
            if(shapeType != null) {
                shapeType.writeEntityNbt(entityTag);
            }
        }

        // put entity type ID under the key "id", or "minecraft:empty" if no shape is equipped (or the shape entity type is invalid)
        tag.putString("id", shape == null ? "minecraft:empty" : Registries.ENTITY_TYPE.getId(shape.getType()).toString());
        tag.put("EntityData", entityTag);
        return tag;
    }

    @Unique
    public void readCurrentShape(NbtCompound tag) {
        Optional<EntityType<?>> type = EntityType.fromNbt(tag);

        // set shape to null (no shape) if the entity id is "minecraft:empty"
        if(tag.getString("id").equals("minecraft:empty")) {
            this.shape = null;
            ((DimensionsRefresher) this).shape_refreshDimensions();
        }

        // if entity type was valid, deserialize entity data from tag
        else if(type.isPresent()) {
            NbtCompound entityTag = tag.getCompound("EntityData");

            // ensure entity data exists
            if(entityTag != null) {
                if(shape == null || !type.get().equals(shape.getType())) {
                    shape = (LivingEntity) type.get().create(world);

                    // refresh player dimensions/hitbox on client
                    ((DimensionsRefresher) this).shape_refreshDimensions();
                }

                shape.readNbt(entityTag);
                shapeType = ShapeType.fromEntityNbt(tag);
            }
        }
    }

    @Unique
    @Override
    public ShapeType<?> get2ndShape() {
        return unlocked;
    }

    @Override
    public void set2ndShape(ShapeType<?> unlocked) {
        this.unlocked= unlocked;
    }

    @Unique
    @Override
    public int getRemainingHostilityTime() {
        return remainingTime;
    }

    @Unique
    @Override
    public void setRemainingHostilityTime(int max) {
        remainingTime = max;
    }

    @Unique
    @Override
    public int getAbilityCooldown() {
        return abilityCooldown;
    }

    @Unique
    @Override
    public void setAbilityCooldown(int abilityCooldown) {
        this.abilityCooldown = abilityCooldown;
    }

    @Unique
    @Override
    public LivingEntity getCurrentShape() {
        return shape;
    }

    @Override
    public ShapeType<?> getCurrentShapeType() {
        return shapeType;
    }

    @Unique
    @Override
    public void setCurrentShape(LivingEntity shape) {
        this.shape = shape;
    }

    @Unique
    @Override
    public boolean updateShapes(@Nullable LivingEntity shape) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        EventResult result = WalkersSwapCallback.EVENT.invoker().swap((ServerPlayerEntity) player, shape);
        if(result.isFalse()) {
            return false;
        }

        this.shape = shape;

        // refresh entity hitbox dimensions
        ((DimensionsRefresher) player).shape_refreshDimensions();

        // shape is valid and scaling health is on; set entity's max health and current health to reflect shape.
        if(shape != null) {
            if (WalkersConfig.getInstance().scalingHealth()) {
                // calculate the current health in percentage, used later
                float currentHealthPercent = player.getHealth() / player.getMaxHealth();

                player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.min(WalkersConfig.getInstance().maxHealth(), shape.getMaxHealth()));
            
                // set health
                if (WalkersConfig.getInstance().percentScalingHealth())
                    player.setHealth(Math.min(currentHealthPercent * player.getMaxHealth(), player.getMaxHealth()));
                else
                    player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
            }
            if (WalkersConfig.getInstance().scalingAttackDamage()) {
                // get shape attack damage, return 1D if value is lower or not existing
                Double shapeAttackDamage = 1D;
                try {
                    shapeAttackDamage = Math.max(shape.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getBaseValue(), shapeAttackDamage);
                }
                catch(Exception ignored) {
                }
                player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.min(WalkersConfig.getInstance().maxAttackDamage(), shapeAttackDamage));
            }
        }

        // If the shape is null (going back to player), set the player's base health value to 20 (default) to clear old changes.
        if(shape == null) {
            float currentHealthPercent = player.getHealth() / player.getMaxHealth();

            if(WalkersConfig.getInstance().scalingHealth()) {
                player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20);
            }

            if(WalkersConfig.getInstance().scalingAttackDamage()) {
                player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(1D);
            }

            // Clear health value if needed
            if (WalkersConfig.getInstance().percentScalingHealth())
                player.setHealth(Math.min(currentHealthPercent * player.getMaxHealth(), player.getMaxHealth()));
            else
                player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
        }

        // update flight properties on player depending on shape
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
        if(Walkers.hasFlyingPermissions((ServerPlayerEntity) player)) {
            FlightHelper.grantFlightTo(serverPlayerEntity);
            player.getAbilities().setFlySpeed(WalkersConfig.getInstance().flySpeed());
            player.sendAbilitiesUpdate();
        } else {
            FlightHelper.revokeFlight(serverPlayerEntity);
            player.getAbilities().setFlySpeed(0.05f);
            player.sendAbilitiesUpdate();
        }

        // If the player is riding a Ravager and changes into an Walkers that cannot ride Ravagers, kick them off.
        if(player.getVehicle() instanceof RavagerEntity && (shape == null || !shape.getType().isIn(WalkersEntityTags.RAVAGER_RIDING))) {
            player.stopRiding();
        }

        // sync with client
        if(!player.world.isClient) {
            PlayerShape.sync((ServerPlayerEntity) player);

            Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) ((ServerWorld) player.world).getChunkManager().threadedAnvilChunkStorage).getEntityTrackers();
            Object tracking = trackers.get(player.getId());
            ((EntityTrackerAccessor) tracking).getListeners().forEach(listener -> {
                PlayerShape.sync((ServerPlayerEntity) player, listener.getPlayer());
            });
        }

        return true;
    }
}
