package tocraft.walkers.api.variant;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.impl.variant.*;
import tocraft.walkers.integrations.Integrations;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class TypeProviderRegistry {
    private static final Map<EntityType<? extends LivingEntity>, TypeProvider<? extends LivingEntity>> VARIANT_BY_TYPE = new LinkedHashMap<>();

    public static void registerDefault() {
        register(EntityType.SHEEP, new SheepTypeProvider());
        register(EntityType.AXOLOTL, new AxolotlTypeProvider());
        register(EntityType.PARROT, new ParrotTypeProvider());
        register(EntityType.FOX, new FoxTypeProvider());
        register(EntityType.CAT, new CatTypeProvider());
        register(EntityType.SLIME, new SlimeTypeProvider());
        register(EntityType.FROG, new FrogTypeProvider());
        register(EntityType.HORSE, new HorseTypeProvider());
        register(EntityType.LLAMA, new LlamaTypeProvider<>());
        register(EntityType.TRADER_LLAMA, new LlamaTypeProvider<>());
        register(EntityType.MAGMA_CUBE, new MagmaCubeTypeProvider());
        register(EntityType.MOOSHROOM, new MushroomCowTypeProvider());
        register(EntityType.PANDA, new PandaTypeProvider());
        register(EntityType.RABBIT, new RabbitTypeProvider());
        register(EntityType.VILLAGER, new VillagerTypeProvider());
        register(EntityType.ZOMBIE_VILLAGER, new ZombieVillagerTypeProvider());
        register(EntityType.TROPICAL_FISH, new TropicalFishTypeProvider());
        register(EntityType.SHULKER, new ShulkerTypeProvider());
        register(EntityType.WOLF, new WolfTypeProvider());

        // handle Integrations
        Integrations.registerTypeProvider();
    }

    public static <T extends LivingEntity> void register(EntityType<T> type, TypeProvider<T> provider) {
        VARIANT_BY_TYPE.put(type, provider);
    }

    public static <T extends LivingEntity> boolean hasProvider(EntityType<T> type) {
        return VARIANT_BY_TYPE.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends LivingEntity> TypeProvider<T> getProvider(EntityType<T> type) {
        return (TypeProvider<T>) VARIANT_BY_TYPE.get(type);
    }

    /**
     * @return a list of every entity type with their registered type provider
     */
    public static List<Pair<EntityType<? extends LivingEntity>, TypeProvider<?>>> getAll() {
        List<Pair<EntityType<? extends LivingEntity>, TypeProvider<?>>> typeList = new ArrayList<>();
        VARIANT_BY_TYPE.forEach((entityType, typeProvider) -> typeList.add(new Pair<>(entityType, typeProvider)));
        return typeList;
    }

    public static void clearAll() {
        VARIANT_BY_TYPE.clear();
    }
}
