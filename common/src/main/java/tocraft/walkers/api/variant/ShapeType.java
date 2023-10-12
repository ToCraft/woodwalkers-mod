package tocraft.walkers.api.variant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import tocraft.walkers.impl.variant.AxolotlTypeProvider;
import tocraft.walkers.impl.variant.CatTypeProvider;
import tocraft.walkers.impl.variant.FoxTypeProvider;
import tocraft.walkers.impl.variant.FrogTypeProvider;
import tocraft.walkers.impl.variant.HorseTypeProvider;
import tocraft.walkers.impl.variant.LlamaTypeProvider;
import tocraft.walkers.impl.variant.ParrotTypeProvider;
import tocraft.walkers.impl.variant.SheepTypeProvider;
import tocraft.walkers.impl.variant.SlimeTypeProvider;
import tocraft.walkers.impl.variant.WolfTypeProvider;

public class ShapeType<T extends LivingEntity> {

	private static final List<EntityType<? extends LivingEntity>> LIVING_TYPE_CASH = new ArrayList<>();
	private static final Map<EntityType<? extends LivingEntity>, TypeProvider<?>> VARIANT_BY_TYPE = new LinkedHashMap<>();
	private final EntityType<T> type;
	private final int variantData;

	static {
		VARIANT_BY_TYPE.put(EntityType.SHEEP, new SheepTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.AXOLOTL, new AxolotlTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.PARROT, new ParrotTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.FOX, new FoxTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.CAT, new CatTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.SLIME, new SlimeTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.FROG, new FrogTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.WOLF, new WolfTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.HORSE, new HorseTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.LLAMA, new LlamaTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.TRADER_LLAMA, new LlamaTypeProvider());
	}

	public ShapeType(EntityType<T> type) {
		this.type = type;
		variantData = getDefaultVariantData(type);
	}

	private int getDefaultVariantData(EntityType<T> type) {
		if (VARIANT_BY_TYPE.containsKey(type)) {
			return VARIANT_BY_TYPE.get(type).getFallbackData();
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
		TypeProvider<T> provider = (TypeProvider<T>) VARIANT_BY_TYPE.get(type);
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
		if (VARIANT_BY_TYPE.containsKey(type)) {
			TypeProvider<Z> typeProvider = (TypeProvider<Z>) VARIANT_BY_TYPE.get(type);
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
	public static <Z extends LivingEntity> ShapeType<Z> from(EntityType<?> entityType, int variant) {
		if (VARIANT_BY_TYPE.containsKey(entityType)) {
			TypeProvider<?> provider = VARIANT_BY_TYPE.get(entityType);
			if (variant < -1 || variant > provider.getRange()) {
				return null;
			}
		}

		return new ShapeType<>((EntityType<Z>) entityType, variant);
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
		TypeProvider<T> typeProvider = (TypeProvider<T>) VARIANT_BY_TYPE.get(type);
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
		TypeProvider<T> provider = (TypeProvider<T>) VARIANT_BY_TYPE.get(type);
		if (provider != null) {
			return provider.modifyText(entity, Component.translatable(type.getDescriptionId()));
		}

		return Component.translatable(type.getDescriptionId());
	}
}
