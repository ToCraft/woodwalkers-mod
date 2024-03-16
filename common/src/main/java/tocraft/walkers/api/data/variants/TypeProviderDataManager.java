package tocraft.walkers.api.data.variants;

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
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.api.variant.TypeProviderRegistry;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Optional;

public class TypeProviderDataManager extends SimpleJsonResourceReloadListener {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();

    public TypeProviderDataManager() {
        super(GSON, Walkers.MODID + "/variants");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        for (Map.Entry<ResourceLocation, JsonElement> mapEntry : map.entrySet()) {
            Map.Entry<EntityType<?>, TypeProvider<?>> convertedEntry = typeProviderFromJson(mapEntry.getKey(), mapEntry.getValue().getAsJsonObject());

            if (convertedEntry.getKey() != null && convertedEntry.getValue() != null) {
                TypeProviderRegistry.register((EntityType<LivingEntity>) convertedEntry.getKey(), (TypeProvider<LivingEntity>) convertedEntry.getValue());

                Walkers.LOGGER.info("{}: {} registered for {}", getClass().getSimpleName(), convertedEntry.getKey(), convertedEntry.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected static Map.Entry<EntityType<?>, TypeProvider<?>> typeProviderFromJson(ResourceLocation id, JsonObject json) {
        Codec<Map.Entry<EntityType<?>, TypeProvider<?>>> codec = RecordCodecBuilder.create((instance) -> instance.group(
                ResourceLocation.CODEC.fieldOf("entity_type").forGetter(o -> BuiltInRegistries.ENTITY_TYPE.getKey(o.getKey())),
                Codec.STRING.optionalFieldOf("required_mod", "").forGetter(o -> ""),
                ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(o -> Optional.of(BuiltInRegistries.ENTITY_TYPE.getKey(o.getKey()))),
                Codec.STRING.optionalFieldOf("type_provider_class").forGetter(o -> Optional.of(o.getValue().getClass().getName())),
                NBTTypeProvider.CODEC.optionalFieldOf("type_provider").forGetter(o -> {
                    if (o.getValue() instanceof NBTTypeProvider<?> nbtTypeProvider)
                        return Optional.of(nbtTypeProvider);
                    else
                        return Optional.empty();
                })
        ).apply(instance, instance.stable((entityType, requiredMod, parent, typeProviderClassOptional, typeProviderOptional) -> {
            if ((requiredMod.isBlank() || Platform.isModLoaded(requiredMod)) && BuiltInRegistries.ENTITY_TYPE.containsKey(entityType)) {
                TypeProvider<?> typeProvider;
                if (parent.isPresent()) {
                    typeProvider = TypeProviderRegistry.getProvider((EntityType<? extends LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(parent.get()));
                } else if (typeProviderClassOptional.isPresent()) {
                    try {
                        typeProvider = Class.forName(typeProviderClassOptional.get()).asSubclass(TypeProvider.class).getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException | ClassNotFoundException e) {
                        Walkers.LOGGER.error("{}: No valid type provider class registered for {}", TypeProviderDataManager.class.getSimpleName(), id);
                        return new SimpleEntry<>(BuiltInRegistries.ENTITY_TYPE.get(entityType), null);
                    }
                } else if (typeProviderOptional.isPresent()) {
                    typeProvider = typeProviderOptional.get();
                } else {
                    Walkers.LOGGER.error("{}: No valid type provider registered for {}", TypeProviderDataManager.class.getSimpleName(), id);
                    return new SimpleEntry<>(BuiltInRegistries.ENTITY_TYPE.get(entityType), null);
                }
                return new SimpleEntry<>(BuiltInRegistries.ENTITY_TYPE.get(entityType), typeProvider);
            } else if (requiredMod.isBlank() || Platform.isModLoaded(requiredMod)) {
                Walkers.LOGGER.info("{}: EntityType not found for {}", TypeProviderDataManager.class.getSimpleName(), id);
            }
            return new SimpleEntry<>(null, null);
        })));
        return Util.getOrThrow(codec.parse(JsonOps.INSTANCE, json), JsonParseException::new);
    }
}
