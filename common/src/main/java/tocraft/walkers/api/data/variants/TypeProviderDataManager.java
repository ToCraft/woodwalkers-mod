package tocraft.walkers.api.data.variants;

import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.data.SynchronizedJsonReloadListener;
import tocraft.craftedcore.platform.PlatformData;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.api.variant.TypeProviderRegistry;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;

public class TypeProviderDataManager extends SynchronizedJsonReloadListener {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();

    public TypeProviderDataManager() {
        super(GSON, Walkers.MODID + "/variants");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onApply(@NotNull Map<ResourceLocation, JsonElement> map) {
        // prevent duplicates and the registration of removed entries
        TypeProviderRegistry.clearAll();
        TypeProviderRegistry.registerDefault();

        for (Map.Entry<ResourceLocation, JsonElement> mapEntry : map.entrySet()) {
            Either<TypeProviderEntry<?>, String> either = typeProviderFromJson(mapEntry.getValue().getAsJsonObject());

            // print error
            if (either.left().isPresent()) {
                TypeProviderEntry<?> typeProviderEntry = either.left().get();

                EntityType<LivingEntity> entityType = (EntityType<LivingEntity>) typeProviderEntry.entityType();
                if (entityType != null) {
                    TypeProviderRegistry.register(entityType, (TypeProvider<LivingEntity>) typeProviderEntry.typeProvider());

                    Walkers.LOGGER.info("{}: {} registered for {}", getClass().getSimpleName(), typeProviderEntry.entityTypeKey(), typeProviderEntry.typeProvider().getClass());
                }
            } else {
                Walkers.LOGGER.warn("Couldn't parse json as type provider: {}", mapEntry.getKey());
            }
        }
    }

    @SuppressWarnings("unchecked")
    /*
     * String is an exception while loading. Can be ignored for normal use (just use Either.left)
     */
    public static final Codec<Either<TypeProviderEntry<?>, String>> TYPE_PROVIDER_LIST_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("entity_type").forGetter(o -> o.left().orElseThrow().entityTypeKey()),
            Codec.STRING.optionalFieldOf("required_mod", "").forGetter(o -> {
                String requiredMod = o.left().orElseThrow().requiredMod();
                if (requiredMod == null) return "";
                else return requiredMod;
            }),
            ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(o -> {
                for (Pair<EntityType<? extends LivingEntity>, TypeProvider<?>> pair : TypeProviderRegistry.getAll()) {
                    if (pair.getSecond() == o.left().orElseThrow().typeProvider()) {
                        return Optional.of(EntityType.getKey(pair.getFirst()));
                    }
                }
                return Optional.empty();
            }),
            Codec.STRING.optionalFieldOf("type_provider_class").forGetter(o -> {
                if (o.left().orElseThrow().typeProvider() instanceof NBTTypeProvider<?>)
                    return Optional.empty();
                else return Optional.of(o.left().orElseThrow().typeProvider().getClass().getName());
            }),
            NBTTypeProvider.CODEC.optionalFieldOf("type_provider").forGetter(o -> {
                if (o.left().orElseThrow().typeProvider() instanceof NBTTypeProvider<?> nbtTypeProvider)
                    return Optional.of(nbtTypeProvider);
                else
                    return Optional.empty();
            })
    ).apply(instance, instance.stable((entityType, requiredMod, parent, typeProviderClassOptional, typeProviderOptional) -> {
        TypeProvider<?> typeProvider;
        if (typeProviderOptional.isPresent()) {
            typeProvider = typeProviderOptional.get();
        } else if (parent.isPresent()) {
            typeProvider = TypeProviderRegistry.getProvider((EntityType<? extends LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(parent.get()).orElseThrow().value());
        } else if (typeProviderClassOptional.isPresent()) {
            try {
                typeProvider = Class.forName(typeProviderClassOptional.get()).asSubclass(TypeProvider.class).getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException | ClassNotFoundException e) {
                return Either.right(TypeProviderDataManager.class.getSimpleName() + ": No valid type provider class registered in %s");
            }
        } else {
            return Either.right(TypeProviderDataManager.class.getSimpleName() + ": No valid type provider registered in %s");
        }
        if (typeProvider != null)
            return Either.left(new TypeProviderEntry<>(entityType, requiredMod, typeProvider));
        else
            return Either.right(TypeProviderDataManager.class.getSimpleName() + ": Error while loading %s");
    })));

    private static Either<TypeProviderEntry<?>, String> typeProviderFromJson(JsonObject json) {
        return TYPE_PROVIDER_LIST_CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(JsonParseException::new);
    }

    @SuppressWarnings("unused")
    public record TypeProviderEntry<L extends LivingEntity>(ResourceLocation entityTypeKey,
                                                            @Nullable String requiredMod,
                                                            TypeProvider<L> typeProvider) {

        public TypeProviderEntry(EntityType<L> entityType, String requiredMod, TypeProvider<L> typeProvider) {
            this(EntityType.getKey(entityType), requiredMod, typeProvider);
        }

        @SuppressWarnings("unchecked")
        @Nullable
        public EntityType<L> entityType() {
            if ((requiredMod() == null || requiredMod().isBlank() || PlatformData.isModLoaded(requiredMod())) && BuiltInRegistries.ENTITY_TYPE.containsKey(entityTypeKey()))
                return (EntityType<L>) BuiltInRegistries.ENTITY_TYPE.get(entityTypeKey()).orElseThrow().value();
            else
                return null;
        }
    }
}
