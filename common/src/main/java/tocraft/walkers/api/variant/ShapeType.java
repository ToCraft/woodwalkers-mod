package tocraft.walkers.api.variant;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.blacklist.EntityBlacklist;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
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

    public ShapeType(T entity) {
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
    public static ShapeType<?> from(CompoundTag compound) {
        ResourceLocation id = new ResourceLocation(compound.getString("EntityID"));
        if (!BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
            return null;
        }

        return from((EntityType<? extends LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(id),
                compound.contains("Variant") ? compound.getInt("Variant") : -1);
    }

    @Nullable
    public static <Z extends LivingEntity> ShapeType<Z> from(EntityType<Z> entityType, int variant) {
        TypeProvider<Z> typeProvider = TypeProviderRegistry.getProvider(entityType);
        if (typeProvider != null) {
            if (variant < -1 || variant > typeProvider.getRange()) {
                return null;
            }
        }

        return new ShapeType<>(entityType, variant);
    }

    public static List<ShapeType<?>> getAllTypes(Level world) {
        if (LIVING_TYPE_CASH.isEmpty()) {
            for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
                try {
                    Entity instance = type.create(world);
                    if (instance instanceof LivingEntity) {
                        LIVING_TYPE_CASH.add((EntityType<? extends LivingEntity>) type);
                    }
                } catch (Exception e) {
                    Walkers.LOGGER.error(e.getLocalizedMessage());
                }
            }
        }

        List<ShapeType<? extends LivingEntity>> types = new ArrayList<>();
        for (EntityType<? extends LivingEntity> type : LIVING_TYPE_CASH) {
            // check blacklist
            if (!EntityBlacklist.isBlacklisted(type)) {
                // check variants
                TypeProvider<?> variant = TypeProviderRegistry.getProvider(type);
                if (variant != null) {
                    for (int i = 0; i <= variant.getRange(); i++) {
                        types.add(new ShapeType<>(type, i));
                    }
                } else {
                    types.add(ShapeType.from(type));
                }
            }
        }

        return types;
    }

    @Deprecated
    public static List<ShapeType<?>> getAllTypes(Level world, boolean includeVariants) {
        List<ShapeType<?>> types = getAllTypes(world);
        List<ShapeType<?>> filteredTypes = new ArrayList<>();
        if (!includeVariants) {
            for (ShapeType<?> type : types) {
                if (filteredTypes.stream().noneMatch(fType -> fType.getEntityType() == type.getEntityType()))
                    filteredTypes.add(type);
            }
        }
        return filteredTypes;
    }

    public CompoundTag writeCompound() {
        CompoundTag compound = new CompoundTag();
        compound.putString("EntityID", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
        compound.putInt("Variant", variantData);
        return compound;
    }

    public EntityType<? extends LivingEntity> getEntityType() {
        return type;
    }

    public T create(Level world) {
        TypeProvider<T> typeProvider = TypeProviderRegistry.getProvider(type);
        if (typeProvider != null) {
            return typeProvider.create(type, world, variantData);
        }

        return type.create(world);
    }

    /**
     * Create the entity based on player data
     */
    public T create(Level world, Player player) {
        TypeProvider<T> typeProvider = TypeProviderRegistry.getProvider(type);
        if (typeProvider != null) {
            return typeProvider.create(type, world, variantData, player);
        }

        return type.create(world);
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

    public static <L extends LivingEntity> Component createTooltipText(L entity) {
        TypeProvider<L> provider = TypeProviderRegistry.getProvider((EntityType<L>) entity.getType());
        if (provider != null) {
            return provider.modifyText(entity, Component.translatable(entity.getType().getDescriptionId()));
        }

        return Component.translatable(entity.getType().getDescriptionId());
    }
}
