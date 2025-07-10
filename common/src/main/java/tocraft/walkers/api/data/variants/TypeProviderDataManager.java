package tocraft.walkers.api.data.variants;

import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tocraft.craftedcore.data.SynchronizedJsonReloadListener;
import dev.tocraft.craftedcore.platform.PlatformData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.variant.NBTTypeProvider;
import tocraft.walkers.api.variant.RegistryTypeProvider;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.api.variant.TypeProviderRegistry;

import java.util.Map;

public class TypeProviderDataManager extends SynchronizedJsonReloadListener {
    public static final Gson GSON = new GsonBuilder().create();

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
            TypeProviderEntry typeProviderEntry = typeProviderFromJson(mapEntry.getValue().getAsJsonObject());

            // Register Variants
            EntityType<LivingEntity> entityType = (EntityType<LivingEntity>) typeProviderEntry.entityType();
            if (entityType != null) {
                TypeProviderRegistry.register(entityType, (TypeProvider<LivingEntity>) typeProviderEntry.typeProvider());
                Walkers.LOGGER.debug("{}: {} registered", getClass().getSimpleName(), typeProviderEntry.entityTypeKey());
            }
        }
    }

    /*
     * String is an exception while loading. Can be ignored for normal use (just use Either.left)
     */
    public static final Codec<TypeProviderEntry> TYPE_PROVIDER_LIST_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("entity_type").forGetter(TypeProviderEntry::entityTypeKey),
            Codec.STRING.optionalFieldOf("required_mod", "").forGetter(o -> {
                String requiredMod = o.requiredMod();
                if (requiredMod == null) return "";
                else return requiredMod;
            }),
            Codec.either(NBTTypeProvider.CODEC, RegistryTypeProvider.CODEC).fieldOf("type_provider").forGetter(o -> o.providerEither)
    ).apply(instance, instance.stable(TypeProviderEntry::new)));

    private static TypeProviderEntry typeProviderFromJson(JsonObject json) {
        return TYPE_PROVIDER_LIST_CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(JsonParseException::new);
    }

    @SuppressWarnings("unused")
    public record TypeProviderEntry(ResourceLocation entityTypeKey,
                                    @Nullable String requiredMod,
                                    Either<NBTTypeProvider<?>, RegistryTypeProvider<?, ?>> providerEither) {

        public TypeProviderEntry(EntityType<?> entityType, String requiredMod, NBTTypeProvider<?> typeProvider) {
            this(EntityType.getKey(entityType), requiredMod, Either.left(typeProvider));
        }

        @Nullable
        public EntityType<?> entityType() {
            if ((requiredMod() == null || requiredMod().isBlank() || PlatformData.isModLoaded(requiredMod())) && BuiltInRegistries.ENTITY_TYPE.containsKey(entityTypeKey()))
                return BuiltInRegistries.ENTITY_TYPE.get(entityTypeKey()).orElseThrow().value();
            else
                return null;
        }

        public TypeProvider<?> typeProvider() {
            return providerEither.map(n -> n, t -> t);
        }
    }
}
