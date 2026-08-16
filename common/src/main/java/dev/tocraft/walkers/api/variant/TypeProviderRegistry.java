package dev.tocraft.walkers.api.variant;

import com.mojang.datafixers.util.Pair;
import dev.tocraft.walkers.impl.variant.*;
import dev.tocraft.walkers.integrations.AbstractIntegration;
import dev.tocraft.walkers.integrations.Integrations;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class TypeProviderRegistry {
    private static final Map<EntityType<? extends LivingEntity>, TypeProvider<? extends LivingEntity>> VARIANT_BY_TYPE = new LinkedHashMap<>();

    @ApiStatus.Internal
    public static void registerDefault() {
        // "old" Type Provider
        register(EntityTypes.CREEPER, new CreeperTypeProvider());
        register(EntityTypes.SHEEP, new SheepTypeProvider());
        register(EntityTypes.AXOLOTL, new AxolotlTypeProvider());
        register(EntityTypes.PARROT, new ParrotTypeProvider());
        register(EntityTypes.FOX, new FoxTypeProvider());
        register(EntityTypes.SLIME, new SlimeTypeProvider());
        register(EntityTypes.HORSE, new HorseTypeProvider());
        register(EntityTypes.LLAMA, new LlamaTypeProvider<>());
        register(EntityTypes.TRADER_LLAMA, new LlamaTypeProvider<>());
        register(EntityTypes.MAGMA_CUBE, new MagmaCubeTypeProvider());
        register(EntityTypes.MOOSHROOM, new MushroomCowTypeProvider());
        register(EntityTypes.PANDA, new PandaTypeProvider());
        register(EntityTypes.RABBIT, new RabbitTypeProvider());
        register(EntityTypes.VILLAGER, new VillagerTypeProvider());
        register(EntityTypes.ZOMBIE_VILLAGER, new ZombieVillagerTypeProvider());
        register(EntityTypes.TROPICAL_FISH, new TropicalFishTypeProvider());
        register(EntityTypes.SHULKER, new ShulkerTypeProvider());

        // Registry Type Provider
        register(EntityTypes.CAT, new RegistryTypeProvider<>(Registries.CAT_VARIANT));
        register(EntityTypes.CHICKEN, new RegistryTypeProvider<>(Registries.CHICKEN_VARIANT));
        register(EntityTypes.COW, new RegistryTypeProvider<>(Registries.COW_VARIANT));
        register(EntityTypes.FROG, new RegistryTypeProvider<>(Registries.FROG_VARIANT));
        register(EntityTypes.PIG, new RegistryTypeProvider<>(Registries.PIG_VARIANT));
        register(EntityTypes.WOLF, new RegistryTypeProvider<>(Registries.WOLF_VARIANT));

        // handle Integrations
        Integrations.registerTypeProvider();
    }

    /**
     * must be called within {@link #registerDefault()} or {@link AbstractIntegration#registerTypeProvider Integration.registerTypeProvider()}}
     */
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
    @ApiStatus.Internal
    public static List<Pair<EntityType<? extends LivingEntity>, TypeProvider<?>>> getAll() {
        List<Pair<EntityType<? extends LivingEntity>, TypeProvider<?>>> typeList = new ArrayList<>();
        VARIANT_BY_TYPE.forEach((entityType, typeProvider) -> typeList.add(new Pair<>(entityType, typeProvider)));
        return typeList;
    }

    @ApiStatus.Internal
    public static void clearAll() {
        VARIANT_BY_TYPE.clear();
    }
}
