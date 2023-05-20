package tocraft.walkers.mixin.player;

import dev.architectury.event.EventResult;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerWalkers;
import tocraft.walkers.api.event.WalkersSwapCallback;
import tocraft.walkers.api.FlightHelper;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.api.variant.WalkersType;
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
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
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
    @Unique private final Set<WalkersType<?>> unlocked = new HashSet<>();
    @Unique private final Set<WalkersType<?>> favorites = new HashSet<>();
    @Unique private int remainingTime = 0;
    @Unique private int abilityCooldown = 0;
    @Unique private LivingEntity walkers = null;
    @Unique private WalkersType<?> walkersType = null;

    private PlayerEntityDataMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void readNbt(NbtCompound tag, CallbackInfo info) {
        unlocked.clear();

        // This tag might exist - it contains old save data for pre-variant Identities.
        // Each entry will be a string with an entity registry ID value.
        NbtList unlockedIdList = tag.getList("UnlockedMorphs", NbtElement.STRING_TYPE);
        unlockedIdList.forEach(entityRegistryID -> {
            Identifier id = new Identifier(entityRegistryID.asString());
            if(Registry.ENTITY_TYPE.containsId(id)) {
                EntityType<?> type = Registry.ENTITY_TYPE.get(id);

                // The variant added from the UnlockedMorphs list will default to the fallback value if needed (eg. Sheep => White)
                // This value will be re-serialize in UnlockedIdentities list, so this is 100% for old save conversions
                unlocked.add(new WalkersType(type));
            } else {
                // TODO: log reading error here
            }
        });

        // This is the new tag for saving Walkers unlock information.
        // It includes metadata for variants.
        NbtList unlockedWalkersList = tag.getList("UnlockedIdentities", NbtElement.COMPOUND_TYPE);
        unlockedWalkersList.forEach(compound -> {
            WalkersType<?> type = WalkersType.from((NbtCompound) compound);
            if(type != null) {
                unlocked.add(type);
            } else {
                // TODO: log reading error here
            }
        });

        // Favorites - OLD TAG containing String IDs
        favorites.clear();
        NbtList favoriteIdList = tag.getList("FavoriteIdentities", NbtElement.STRING_TYPE);
        favoriteIdList.forEach(registryID -> {
            Identifier id = new Identifier(registryID.asString());
            if(Registry.ENTITY_TYPE.containsId(id)) {
                EntityType<?> type = Registry.ENTITY_TYPE.get(id);
                favorites.add(new WalkersType(type));
            }
        });

        // Favorites - NEW TAG for updated variant compound data
        NbtList favoriteTypeList = tag.getList("FavoriteIdentitiesV2", NbtElement.STRING_TYPE);
        favoriteTypeList.forEach(compound -> {
            WalkersType<?> type = WalkersType.from((NbtCompound) compound);
            if(type != null) {
                favorites.add(type);
            }
        });

        // Abilities
        abilityCooldown = tag.getInt(ABILITY_COOLDOWN_KEY);

        // Hostility
        remainingTime = tag.getInt("RemainingHostilityTime");

        // Current Walkers
        readCurrentWalkers(tag.getCompound("CurrentWalkers"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void writeNbt(NbtCompound tag, CallbackInfo info) {
        // Write 'Unlocked' Walkers data
        {
            NbtList idList = new NbtList();
            unlocked.forEach(walkers -> idList.add(walkers.writeCompound()));

            // This was "UnlockedMorphs" in previous versions, but it has been changed with the introduction of variants.
            tag.put("UnlockedIdentities", idList);
        }

        // Favorites
        {
            NbtList idList = new NbtList();
            favorites.forEach(entityId -> idList.add(entityId.writeCompound()));
            tag.put("FavoriteIdentitiesV2", idList);
        }

        // Abilities
        tag.putInt(ABILITY_COOLDOWN_KEY, abilityCooldown);

        // Hostility
        tag.putInt("RemainingHostilityTime", remainingTime);

        // Current Walkers
        tag.put("CurrentWalkers", writeCurrentWalkers(new NbtCompound()));
    }

    @Unique
    private NbtCompound writeCurrentWalkers(NbtCompound tag) {
        NbtCompound entityTag = new NbtCompound();

        // serialize current walkers data to tag if it exists
        if(walkers != null) {
            walkers.writeNbt(entityTag);
            if(walkersType != null) {
                walkersType.writeEntityNbt(entityTag);
            }
        }

        // put entity type ID under the key "id", or "minecraft:empty" if no walkers is equipped (or the walkers entity type is invalid)
        tag.putString("id", walkers == null ? "minecraft:empty" : Registry.ENTITY_TYPE.getId(walkers.getType()).toString());
        tag.put("EntityData", entityTag);
        return tag;
    }

    @Unique
    public void readCurrentWalkers(NbtCompound tag) {
        Optional<EntityType<?>> type = EntityType.fromNbt(tag);

        // set walkers to null (no walkers) if the entity id is "minecraft:empty"
        if(tag.getString("id").equals("minecraft:empty")) {
            this.walkers = null;
            ((DimensionsRefresher) this).walkers_refreshDimensions();
        }

        // if entity type was valid, deserialize entity data from tag
        else if(type.isPresent()) {
            NbtCompound entityTag = tag.getCompound("EntityData");

            // ensure entity data exists
            if(entityTag != null) {
                if(walkers == null || !type.get().equals(walkers.getType())) {
                    walkers = (LivingEntity) type.get().create(world);

                    // refresh player dimensions/hitbox on client
                    ((DimensionsRefresher) this).walkers_refreshDimensions();
                }

                walkers.readNbt(entityTag);
                walkersType = WalkersType.fromEntityNbt(tag);
            }
        }
    }

    @Unique
    @Override
    public Set<WalkersType<?>> getUnlocked() {
        return unlocked;
    }

    @Override
    public void setUnlocked(Set<WalkersType<?>> unlocked) {
        this.unlocked.clear();
        this.unlocked.addAll(unlocked);
    }

    @Unique
    @Override
    public Set<WalkersType<?>> getFavorites() {
        return favorites;
    }

    @Override
    public void setFavorites(Set<WalkersType<?>> favorites) {
        this.favorites.clear();
        this.favorites.addAll(favorites);
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
    public LivingEntity getWalkers() {
        return walkers;
    }

    @Override
    public WalkersType<?> getWalkersType() {
        return walkersType;
    }

    @Unique
    @Override
    public void setWalkers(LivingEntity walkers) {
        this.walkers = walkers;
    }

    @Unique
    @Override
    public boolean updateWalkers(@Nullable LivingEntity walkers) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        EventResult result = WalkersSwapCallback.EVENT.invoker().swap((ServerPlayerEntity) player, walkers);
        if(result.isFalse()) {
            return false;
        }

        this.walkers = walkers;

        // refresh entity hitbox dimensions
        ((DimensionsRefresher) player).walkers_refreshDimensions();

        // Walkers is valid and scaling health is on; set entity's max health and current health to reflect walkers.
        if(walkers != null && WalkersConfig.getInstance().scalingHealth()) {
            player.setHealth(Math.min(player.getHealth(), walkers.getMaxHealth()));
            player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.min(WalkersConfig.getInstance().maxHealth(), walkers.getMaxHealth()));
        }

        // If the walkers is null (going back to player), set the player's base health value to 20 (default) to clear old changes.
        if(walkers == null) {
            if(WalkersConfig.getInstance().scalingHealth()) {
                player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20);
            }

            // Clear health value if needed
            player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
        }

        // update flight properties on player depending on walkers
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
        if(player.getVehicle() instanceof RavagerEntity && (walkers == null || !walkers.getType().isIn(WalkersEntityTags.RAVAGER_RIDING))) {
            player.stopRiding();
        }

        // sync with client
        if(!player.world.isClient) {
            PlayerWalkers.sync((ServerPlayerEntity) player);

            Int2ObjectMap<Object> trackers = ((ThreadedAnvilChunkStorageAccessor) ((ServerWorld) player.world).getChunkManager().threadedAnvilChunkStorage).getEntityTrackers();
            Object tracking = trackers.get(player.getId());
            ((EntityTrackerAccessor) tracking).getListeners().forEach(listener -> {
                PlayerWalkers.sync((ServerPlayerEntity) player, listener.getPlayer());
            });
        }

        return true;
    }
}