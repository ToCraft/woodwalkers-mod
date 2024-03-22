package tocraft.walkers.api.variant;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.impl.variant.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class TypeProviderRegistry {
    public static final Map<EntityType<? extends LivingEntity>, TypeProvider<? extends LivingEntity>> VARIANT_BY_TYPE = new LinkedHashMap<>();

    static {
        register(EntityType.SHEEP, new SheepTypeProvider());
        register(EntityType.AXOLOTL, new AxolotlTypeProvider());
        register(EntityType.PARROT, new ParrotTypeProvider());
        register(EntityType.CAT, new CatTypeProvider());
        register(EntityType.SLIME, new SlimeTypeProvider());
        register(EntityType.HORSE, new HorseTypeProvider());
        register(EntityType.LLAMA, new LlamaTypeProvider<>());
        register(EntityType.TRADER_LLAMA, new LlamaTypeProvider<>());
        register(EntityType.GOAT, new GoatTypeProvider());
        register(EntityType.MAGMA_CUBE, new MagmaCubeTypeProvider());
        register(EntityType.PANDA, new PandaTypeProvider());
        register(EntityType.RABBIT, new RabbitTypeProvider());
        register(EntityType.VILLAGER, new VillagerTypeProvider());
        register(EntityType.ZOMBIE_VILLAGER, new ZombieVillagerTypeProvider());
        register(EntityType.SHULKER, new ShulkerTypeProvider());
        register(EntityType.TROPICAL_FISH, new TropicalFishTypeProvider());
        register(EntityType.MOOSHROOM, new MushroomCowTypeProvider());
    }

    public static <T extends LivingEntity> void register(EntityType<T> type, TypeProvider<T> provider) {
        VARIANT_BY_TYPE.put(type, provider);
    }

    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> TypeProvider<T> getProvider(EntityType<T> type) {
        return (TypeProvider<T>) VARIANT_BY_TYPE.get(type);
    }
}
