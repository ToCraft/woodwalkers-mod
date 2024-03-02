package tocraft.walkers.api.data.abilities;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.ability.ShapeAbility;
import tocraft.walkers.api.data.variants.TypeProviderDataManager;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.function.Predicate;

public class AbilityDataManager extends SimpleJsonResourceReloadListener {
    private static final String DEFAULT_PACKAGE = "tocraft.walkers.ability.impl";
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();

    public AbilityDataManager() {
        super(GSON, Walkers.MODID + "/abilities");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        for (Map.Entry<ResourceLocation, JsonElement> mapEntry : map.entrySet()) {
            Map.Entry<EntityType<?>, ShapeAbility<?>> convertedEntry = abilityEntryFromJson(mapEntry.getValue().getAsJsonObject());

            AbilityRegistry.register((Predicate<LivingEntity>) entity -> entity.getType().equals(convertedEntry.getKey()), convertedEntry.getValue());

            Walkers.LOGGER.info("{}: {} registered for {}", getClass().getSimpleName(), convertedEntry.getKey(), convertedEntry.getValue());
        }
    }

    protected static Map.Entry<EntityType<?>, ShapeAbility<?>> abilityEntryFromJson(JsonObject json) {
        Codec<Map.Entry<EntityType<?>, ShapeAbility<?>>> codec = RecordCodecBuilder.create((instance) -> instance.group(
                ResourceLocation.CODEC.fieldOf("entity_type").forGetter(o -> Registry.ENTITY_TYPE.getKey(o.getKey())),
                Codec.STRING.fieldOf("ability_class").forGetter(o -> o.getValue().getClass().getName())
        ).apply(instance, instance.stable((entityType, shapeAbility) -> {
            if (Registry.ENTITY_TYPE.containsKey(entityType)) {
                try {
                    String abilityClassName = shapeAbility.contains(".") ? shapeAbility : DEFAULT_PACKAGE + "." + shapeAbility;
                    return new SimpleEntry<EntityType<?>, ShapeAbility<?>>(Registry.ENTITY_TYPE.get(entityType), Class.forName(abilityClassName).asSubclass(ShapeAbility.class).getDeclaredConstructor().newInstance());
                } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                         IllegalAccessException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Walkers.LOGGER.info("{}: EntityType not found for {}", TypeProviderDataManager.class.getSimpleName(), entityType);
                return new SimpleEntry<>(null, null);
            }
        })));
        return codec.parse(JsonOps.INSTANCE, json).getOrThrow(false, JsonParseException::new);
    }
}
