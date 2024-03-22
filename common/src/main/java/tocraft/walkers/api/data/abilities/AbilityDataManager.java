package tocraft.walkers.api.data.abilities;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.platform.Platform;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
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

            AbilityRegistry.registerByPredicate((Predicate<LivingEntity>) entity -> entity.getType().equals(convertedEntry.getKey()), convertedEntry.getValue());

            Walkers.LOGGER.info("{}: {} registered for {}", getClass().getSimpleName(), convertedEntry.getKey(), convertedEntry.getValue());
        }
    }

    protected static Map.Entry<EntityType<?>, ShapeAbility<?>> abilityEntryFromJson(JsonObject json) {
        Codec<Map.Entry<EntityType<?>, ShapeAbility<?>>> codec = RecordCodecBuilder.create((instance) -> instance.group(
                ResourceLocation.CODEC.fieldOf("entity_type").forGetter(o -> BuiltInRegistries.ENTITY_TYPE.getKey(o.getKey())),
                Codec.STRING.optionalFieldOf("required_mod", "").forGetter(o -> ""),
                Codec.STRING.fieldOf("ability_class").forGetter(o -> o.getValue().getClass().getName())
        ).apply(instance, instance.stable((entityType, requiredMod, shapeAbility) -> {
            if ((requiredMod.isBlank() || Platform.isModLoaded(requiredMod)) && BuiltInRegistries.ENTITY_TYPE.containsKey(entityType)) {
                try {
                    String abilityClassName = shapeAbility.contains(".") ? shapeAbility : DEFAULT_PACKAGE + "." + shapeAbility;
                    return new SimpleEntry<EntityType<?>, ShapeAbility<?>>(BuiltInRegistries.ENTITY_TYPE.get(entityType), Class.forName(abilityClassName).asSubclass(ShapeAbility.class).getDeclaredConstructor().newInstance());
                } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                         IllegalAccessException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            } else if (requiredMod.isBlank() || Platform.isModLoaded(requiredMod)) {
                Walkers.LOGGER.info("{}: EntityType not found for {}", TypeProviderDataManager.class.getSimpleName(), entityType);
            }
            return new SimpleEntry<>(null, null);
        })));
        return Util.getOrThrow(codec.parse(JsonOps.INSTANCE, json), JsonParseException::new);
    }
}
