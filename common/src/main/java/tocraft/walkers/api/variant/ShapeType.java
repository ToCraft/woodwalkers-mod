package tocraft.walkers.api.variant;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.registry.WalkersEntityTags;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class ShapeType<T extends LivingEntity> {

    private static final List<EntityType<? extends LivingEntity>> LIVING_TYPE_CASH = new ArrayList<>();
    private final EntityType<T> type;
    private final int variantData;

    public ShapeType(EntityType<T> type) {
        this.type = type;
        variantData = getDefaultVariantData(type);
    }

    private int getDefaultVariantData(EntityType<T> type) {
        TypeProvider<T> provider = TypeProviderRegistry.getProvider(type);
        if (provider != null) {
            return provider.getFallbackData();
        } else {
            return -1;
        }
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

    @Nullable
    public static <Z extends LivingEntity> ShapeType<Z> from(Z entity) {
        if (entity == null) {
            return null;
        }

        EntityType<Z> type = (EntityType<Z>) entity.getType();
        TypeProvider<Z> typeProvider = TypeProviderRegistry.getProvider(type);
        if (typeProvider != null) {
            return typeProvider.create(type, entity);
        }

        return new ShapeType<>((EntityType<Z>) entity.getType());
    }

    @Nullable
    public static ShapeType<?> from(CompoundTag compound) {
        ResourceLocation id = new ResourceLocation(compound.getString("EntityID"));
        if (!BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
            return null;
        }

        return new ShapeType(BuiltInRegistries.ENTITY_TYPE.get(id),
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

        return new ShapeType<>((EntityType<Z>) entityType, variant);
    }

    @Deprecated
    public static List<ShapeType<?>> getAllTypes(Level world) {
        return getAllTypes(world, true);
    }

    public static List<ShapeType<?>> getAllTypes(Level world, boolean includeVariants) {
        if (LIVING_TYPE_CASH.isEmpty()) {
            for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
                Entity instance = type.create(world);
                if (instance instanceof LivingEntity) {
                    LIVING_TYPE_CASH.add((EntityType<? extends LivingEntity>) type);
                }
            }
        }

        List<ShapeType<? extends LivingEntity>> types = new ArrayList<>();
        for (EntityType<? extends LivingEntity> type : LIVING_TYPE_CASH) {
            // check blacklist
            if (!type.is(WalkersEntityTags.BLACKLISTED)) {
                // check variants
                TypeProvider<?> variant = TypeProviderRegistry.getProvider(type);
                if (variant != null && includeVariants) {
                    for (int i = 0; i <= variant.getRange(); i++) {
                        types.add(new ShapeType(type, i));
                    }
                } else {
                    types.add(new ShapeType(type));
                }
            }
        }

        return types;
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

    public void writeEntityNbt(CompoundTag tag) {
        CompoundTag inner = writeCompound();
        tag.put("ShapeType", inner);
    }

    public static ShapeType<?> fromEntityNbt(CompoundTag tag) {
        return from(tag.getCompound("ShapeType"));
    }

    public Component createTooltipText(T entity) {
        TypeProvider<T> provider = TypeProviderRegistry.getProvider(type);
        if (provider != null) {
            return provider.modifyText(entity, Component.translatable(type.getDescriptionId()));
        }

        return Component.translatable(type.getDescriptionId());
    }
}
