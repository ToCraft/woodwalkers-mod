package dev.tocraft.walkers.api.variant;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.api.blacklist.EntityBlacklist;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({"unchecked", "unused"})
public class ShapeType<T extends LivingEntity> {

    private static final List<EntityType<? extends LivingEntity>> LIVING_TYPE_CASH = new ArrayList<>();
    private final EntityType<T> type;
    private final int variantData;

    public static <Z extends LivingEntity> int getDefaultVariantData(EntityType<Z> type) {
        TypeProvider<Z> provider = TypeProviderRegistry.getProvider(type);
        if (provider != null) {
            return provider.getFallbackData();
        } else {
            return -1;
        }
    }

    public ShapeType(EntityType<T> type) {
        this(type, getDefaultVariantData(type));
    }

    public ShapeType(EntityType<T> type, int variantData) {
        this.type = type;
        this.variantData = variantData;
    }

    public ShapeType(@NotNull T entity) {
        this.type = (EntityType<T>) entity.getType();

        // Discover variant data based on entity NBT data.
        @Nullable
        TypeProvider<T> provider = TypeProviderRegistry.getProvider(type);
        if (provider != null) {
            variantData = provider.getVariantData(entity);
        } else {
            variantData = getDefaultVariantData(type);
        }
    }

    public static <Z extends LivingEntity> @NotNull ShapeType<Z> from(EntityType<Z> entityType) {
        return new ShapeType<>(entityType, getDefaultVariantData(entityType));
    }

    @Nullable
    public static <Z extends LivingEntity> ShapeType<Z> from(Z entity) {
        if (entity == null) {
            return null;
        }

        return new ShapeType<>(entity);
    }

    @Nullable
    public static ShapeType<?> from(@NotNull ValueInput in) {
        Optional<String> str = in.getString("EntityID");
        if (str.isEmpty()) {
            return null;
        }
        ResourceLocation id = ResourceLocation.parse(str.get());
        if (!BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
            return null;
        }

        return from((EntityType<? extends LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(id).orElseThrow().value(), in.getInt("Variant").orElse(-1));
    }

    @Nullable
    public static ShapeType<?> from(@NotNull CompoundTag compound) {
        Optional<String> str = compound.getString("EntityID");
        if (str.isEmpty()) {
            return null;
        }
        ResourceLocation id = ResourceLocation.parse(str.get());
        if (!BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
            return null;
        }

        return from((EntityType<? extends LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(id).orElseThrow().value(), compound.getInt("Variant").orElse(-1));
    }

    @Nullable
    public static <Z extends LivingEntity> ShapeType<Z> from(EntityType<Z> entityType, int variant) {
        if (variant < -1) {
            return null;
        }

        return new ShapeType<>(entityType, variant);
    }

    public static <T extends LivingEntity> @NotNull List<ShapeType<T>> getAllTypes(EntityType<T> entityType, Level level) {
        List<ShapeType<T>> types = new ArrayList<>();
        // check blacklist
        if (!EntityBlacklist.isBlacklisted(entityType)) {
            // check variants
            TypeProvider<?> variant = TypeProviderRegistry.getProvider(entityType);
            if (variant != null) {
                for (int i = 0; i < variant.size(level); i++) {
                    types.add(new ShapeType<>(entityType, i));
                }
            } else {
                types.add(ShapeType.from(entityType));
            }
        }
        return types;
    }

    public static @NotNull List<ShapeType<?>> getAllTypes(Level world) {
        if (LIVING_TYPE_CASH.isEmpty()) {
            for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
                try {
                    Entity instance = type.create(world, EntitySpawnReason.LOAD);
                    if (instance instanceof LivingEntity) {
                        LIVING_TYPE_CASH.add((EntityType<? extends LivingEntity>) type);
                    }
                } catch (Exception e) {
                    Walkers.LOGGER.error("{}: Caught an exception while getting shape types for entity type + {}: {}", ShapeType.class.getSimpleName(), type.toShortString(), e);
                }
            }
        }

        List<ShapeType<? extends LivingEntity>> types = new ArrayList<>();
        for (EntityType<? extends LivingEntity> type : LIVING_TYPE_CASH) {
            types.addAll(getAllTypes(type, world));
        }

        return types;
    }

    public CompoundTag writeCompound() {
        CompoundTag compound = new CompoundTag();
        compound.putString("EntityID", EntityType.getKey(type).toString());
        compound.putInt("Variant", variantData);
        return compound;
    }

    public EntityType<? extends LivingEntity> getEntityType() {
        return type;
    }

    /**
     * Create the entity based on player data
     */
    public T create(Level world, Player player) {
        TypeProvider<T> typeProvider = TypeProviderRegistry.getProvider(type);
        if (typeProvider != null && variantData < typeProvider.size(world)) {
            return typeProvider.create(type, world, player, variantData);
        }

        return type.create(world, EntitySpawnReason.LOAD);
    }

    public int getVariantData() {
        return variantData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ShapeType<?> that = (ShapeType<?>) o;
        return variantData == that.variantData && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, variantData);
    }

    public static <L extends LivingEntity> Component createTooltipText(@NotNull L entity) {
        TypeProvider<L> provider = TypeProviderRegistry.getProvider((EntityType<L>) entity.getType());
        if (provider != null) {
            return provider.modifyText(entity, Component.translatable(entity.getType().getDescriptionId()));
        }

        return Component.translatable(entity.getType().getDescriptionId());
    }
}
