package tocraft.walkers.api.variant;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.impl.variant.*;

public class TypeProviderRegistry {
	private static final Map<EntityType<? extends LivingEntity>, TypeProvider<? extends LivingEntity>> VARIANT_BY_TYPE = new HashMap<>();
	
	static {
		VARIANT_BY_TYPE.put(EntityType.SHEEP, new SheepTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.AXOLOTL, new AxolotlTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.PARROT, new ParrotTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.CAT, new CatTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.SLIME, new SlimeTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.LLAMA, new LlamaTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.TRADER_LLAMA, new LlamaTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.GOAT, new GoatTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.MAGMA_CUBE, new MagmaCubeTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.MOOSHROOM, new MushroomCowTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.PANDA, new PandaTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.RABBIT, new RabbitTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.VILLAGER, new VillagerTypeProvider());
		VARIANT_BY_TYPE.put(EntityType.ZOMBIE_VILLAGER, new ZombieVillagerTypeProvider());
	}
	
	public static <T extends LivingEntity> void register(EntityType<T> type, TypeProvider<T> provider) {
		VARIANT_BY_TYPE.put(type, provider);
	}
	
	public static <T extends LivingEntity> void unregister(EntityType<T> type) {
		VARIANT_BY_TYPE.remove(type);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends LivingEntity> TypeProvider<T> getProvider(EntityType<T> type) {
		return (TypeProvider<T>) VARIANT_BY_TYPE.get(type);
	}
}
